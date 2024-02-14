package org.eurecat.pathocert.backend.emergency.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.eurecat.pathocert.backend.emergency.model.EmergenciesMultiselectOptions;
import org.eurecat.pathocert.backend.emergency.repository.EmergencyRepository;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentService;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentServiceInt;
import org.eurecat.pathocert.backend.users.model.UserRole;
import org.eurecat.pathocert.backend.users.repository.UserRepository;
import org.eurecat.pathocert.backend.users.service.UserDetailsImpl;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


@BasePathAwareController
@RequestMapping(value = "/api/emergencies/")
public class EmergenciesEndpoint {

    @Autowired
    private EmergencyRepository emergencyRepository;

    @Autowired
    private NeoDocumentServiceInt neoDocumentService;

    @Autowired
    private UserRepository userRepository;

    @NotNull
    static private <T> HashMap<T, T> listToHashMap(List<T> xs) {
        var map = new HashMap<T, T>();
        for (T s : xs) {
            map.put(s, s);
        }
        return map;
    }

    @GetMapping(path = "/my")
    @ResponseBody
    public ResponseEntity<CollectionModel<EntityModel<Emergency>>> getAuthorizedArchived(boolean archived, String username) {

        /*
        var principal_f = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal_f);
        if (principal_f.equals("anonymousUser")) {
            return ResponseEntity.ok(CollectionModel.of(new LinkedList<>()));
        }
        final var principal = (String) principal_f;
        var user_opt = userRepository.findByUsername(principal);
        System.out.println(user_opt);
        if (user_opt.isEmpty()) {
            return ResponseEntity.ok(CollectionModel.of(new LinkedList<>()));
        }
        final var user = user_opt.get();
        System.out.println(user.getUsername());
         */

        var listOfEntities = emergencyRepository.findByReportingUsernameAndArchived(username, archived);
        listOfEntities
                .sort(Comparator.comparing(Emergency::getReportDate).reversed());
        var entities = listOfEntities
                .stream().map(EntityModel::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(entities));
    }

    @GetMapping(path = "/selectable-values")
    @ResponseBody
    public ResponseEntity<EntityModel<EmergenciesMultiselectOptions>> getSelectableValues() {
        var query = "MATCH (n:ContaminantD) return n.name AS label UNION MATCH (n2:Contaminant) return n2.name AS label";
        var contaminants = neoDocumentService.executeLabelTransaction(query);
        query = "MATCH (n:SymptomD) return n.name AS label UNION MATCH (n2:Symptom) return n2.name AS label";
        var symptoms = neoDocumentService.executeLabelTransaction(query);
        query = "MATCH (n:Detection) return n.description AS label";
        var detection = neoDocumentService.executeLabelTransaction(query);
        HashMap<String, String> contmap = listToHashMap(contaminants);
        HashMap<String, String> sympmap = listToHashMap(symptoms);
        HashMap<String, String> infmap = listToHashMap(detection);
        var multiselect = new EmergenciesMultiselectOptions(sympmap, infmap, contmap);
        return ResponseEntity.ok(EntityModel.of(multiselect));
    }

    @PostMapping(path = "/send-to-ware")
    @ResponseBody
    public ResponseEntity<String> sendAssessmentToPathoWARE(@RequestHeader("scenario") String scenario, @RequestHeader("x-token") String x_token, @RequestBody Map<String, Object> data) {
        String emergencyJSON = generateWAREJSONString(data);
        // System.out.println("EDITED: " + emergencyJSON);
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dataconnector-pathocert.opsi.lecce.it/dataconnector/api/v1/pathothreat/alert/?scenario=" + scenario))
                .method("POST", HttpRequest.BodyPublishers.ofString(emergencyJSON.toString()))
                .header("x-token", x_token)
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assert response != null;
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

    public String prettify(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        return writer.writeValueAsString(mapper.readTree(jsonString));
    }

    @PostMapping(path = "/export-to-file")
    @ResponseBody
    public ResponseEntity<String> exportAssessmentToFile(@RequestBody Map<String, Object> data, HttpServletResponse response) throws Exception {
        String emergencyJSON = generateWAREJSONString(data);

        PrintWriter writer = response.getWriter();

        writer.write(prettify(emergencyJSON));

        response.setContentType("application/json");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + (String) data.get("name") + "_" + new Date().getTime() + ".json";
        response.setHeader(headerKey, headerValue);
        return ResponseEntity.ok().build();
    }

    private String generateWAREJSONString(Map<String, Object> data) {
        StringBuilder emergencyJSON = new StringBuilder("{");
        for (String key : data.keySet()) {
            if (key.equals("locationLat") || key.equals("locationLon")) {
                continue;
            }
            emergencyJSON.append("\"").append(key).append("\":");
            switch (key) {
                case "description":
                    emergencyJSON.append("\"").append(cleanDescription((String) data.get(key))).append("\"");
                    break;
                case "owner":
                    emergencyJSON.append("[\"").append((String) data.get(key)).append("\"]");
                    break;
                case "validTo":
                    emergencyJSON.append("null");
                    break;
                default:
                    String theText = (String) data.get(key);
                    theText = theText.replaceAll("<.*?>", "");
                    for (char character : "()<>;='\"".toCharArray()) {
                        theText = theText.replace(String.valueOf(character), "");
                    }
                    emergencyJSON.append("\"").append(theText).append("\"");
                    break;
            }
            emergencyJSON.append(",");
        }
        emergencyJSON.append("\"").append("location").append("\":{\"type\":\"Point\",\"coordinates\":[")
                .append((String) data.get("locationLon")).append(",").append((String) data.get("locationLat"))
                .append("]}}");
        return String.valueOf(emergencyJSON);
    }

    private String cleanDescription(String description) {
        description = description.replace("  ", "");
        description = description.replace("\n", "");
        description = description.replace("</p>", ". ");
        description = description.replace("</li>", ",");
        description = description.replace("</ul>", ". ");
        description = description.replaceAll("<.*?>", "");
        for (char character : "()<>;='\"".toCharArray()) {
            description = description.replace(String.valueOf(character), "");
        }
        description = description.replace(",.", ".");
        return description;
    }

}
