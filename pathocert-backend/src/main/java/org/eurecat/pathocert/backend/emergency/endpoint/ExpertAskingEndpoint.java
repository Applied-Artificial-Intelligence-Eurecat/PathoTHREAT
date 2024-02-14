package org.eurecat.pathocert.backend.emergency.endpoint;

import org.eurecat.pathocert.backend.emergency.model.ExNode;
import org.eurecat.pathocert.backend.emergency.model.GraphItem;
import org.eurecat.pathocert.backend.emergency.service.ExpertService;
import org.eurecat.pathocert.backend.emergency.service.ExpertServiceInt;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@BasePathAwareController
@RequestMapping(value = "/api/expert/")
public class ExpertAskingEndpoint {

    @Autowired
    private ExpertServiceInt expertService;
    @Autowired
    private NeoDocumentServiceInt neoDocumentService;

    @GetMapping(path = "search-terms")
    @ResponseBody
    public ResponseEntity<String> getSearchResults(@RequestParam("subject") String subject, @RequestParam("desiredOutput") String desiredOutput) {
        List<ExNode> nodes = expertService.getTerm1andTerm2Search(subject, desiredOutput);
        StringBuilder result = new StringBuilder("<p>");
        for (ExNode node : nodes) {
            result.append("Name: ").append(node.getName()).append("<br>");
        }
        result.append("</p>");
        var res = result.toString();
        return ResponseEntity.ok(res);
    }

    @GetMapping(path = "values")
    @ResponseBody
    public ResponseEntity<CollectionModel<String>> getAllExpertValues() {
        // Per al subject de la expert search
        var query = "MATCH (n) WHERE n:Event OR n:CascadingWNEvent OR n:ProducedEvent OR n:Contaminant " +
                "OR n:EffectWaterTaste OR n:EffectWaterOdor OR n:EffectWater OR n:ContaminantFamily " +
                "OR n:ContaminantTreatment OR n:EffectHealth OR n:Symptom OR n:ContaminantType OR n:ContaminantMitigation " +
                "return n.name as label";
        List<String> values = neoDocumentService.executeLabelTransaction(query);
        var sortedvals = new LinkedList<String>();
        values.stream().sorted().forEach(sortedvals::add);
        return ResponseEntity.ok(CollectionModel.of(sortedvals));
    }

    @GetMapping(path = "labels")
    @ResponseBody
    public ResponseEntity<CollectionModel<String>> getAllExpertLabels() {
        // Per al desiredOutput de la expert search
        List<String> values = Arrays.asList("Event", "CascadingWNEvent", "ProducedEvent", "Contaminant",
                "EffectWaterTaste", "EffectWaterOdor", "EffectWater", "ContaminantFamily",
                "ContaminantTreatment", "EffectHealth", "Symptom", "ContaminantType", "ContaminantMitigation");
        var sortedvals = new LinkedList<String>();
        values.stream().sorted().forEach(sortedvals::add);
        return ResponseEntity.ok(CollectionModel.of(sortedvals));
    }

    @GetMapping(path = "values/{type}")
    @ResponseBody
    public ResponseEntity<CollectionModel<String>> getExpertValues(@PathVariable("type") String type) {
        // Event, SymptomE, ContaminantE, CascadingWNEvent
        var query = "MATCH (n) WHERE n:" + type + " return n.name as label";
        List<String> values = neoDocumentService.executeLabelTransaction(query);
        return ResponseEntity.ok(CollectionModel.of(values));
    }

    @GetMapping(path = "values/dropdown/{type}")
    @ResponseBody
    public ResponseEntity<CollectionModel<String>> getDropdownValues(@PathVariable("type") String type) {
        // Event, SymptomE, ContaminantE, CascadingWNEvent
        var query = "MATCH (n) WHERE n:" + type + " return n.description as label";
        List<String> values = neoDocumentService.executeLabelTransaction(query);
        List<String> results = new LinkedList<>();
        Collections.sort(values);
        for (String v : values) {
            results.add(v.substring(0, 1).toUpperCase() + v.substring(1).toLowerCase());
        }
        return ResponseEntity.ok(CollectionModel.of(results));
    }

    @GetMapping(path = "labels/{subject}")
    @ResponseBody
    public ResponseEntity<CollectionModel<String>> getGoodLabels(@PathVariable("subject") String subject) {
        var labels = getAllExpertLabels();
        var goodLabels = new LinkedList<String>();
        if (labels.hasBody()) {
            for (String next : Objects.requireNonNull(labels.getBody())) {
                var result = getSearchResults(subject, next);
                if (Objects.requireNonNull(result.getBody()).contains("<br>")) {
                    goodLabels.add(next);
                }
            }
        }
        return ResponseEntity.ok(CollectionModel.of(goodLabels));
    }

    @GetMapping(path = "graph-data")
    @ResponseBody
    public ResponseEntity<CollectionModel<GraphItem>> getGraph() {
        var items = new LinkedList<GraphItem>();
        var labels = getAllExpertLabels();
        if (labels.hasBody()) {
            for (String next : Objects.requireNonNull(labels.getBody())) {
                var values = getExpertValues(next).getBody();
                if (values == null) {
                    continue;
                }
                values.forEach(v -> {
                    items.add(new GraphItem("nodes", v, next));
                });
            }
        }
        List<String> relation_types = Arrays.asList("CAN_PRODUCE",
                "CAN_CAUSE", "CAN_CASCADE", "HAS_EFFECT", "HAS_SYMPTOM",
                "FAMILY", "IS_TREATED", "CAN_SMELL", "CAN_TASTE",
                "TYPE", "CAN_BE_MITIGATED");
        for (String rel_type : relation_types) {
            List<GraphItem> relations = expertService.getRelationsOfType(rel_type);
            relations.forEach(r -> {
                var item = new GraphItem("edges", r.source, r.target);
                item.label = rel_type;
                items.add(item);
            });
        }
        System.out.println("ITEMS: " + items.size());
        return ResponseEntity.ok(CollectionModel.of(items));
    }

    @GetMapping(path = "graph-data-search")
    @ResponseBody
    public ResponseEntity<CollectionModel<GraphItem>> getGraphSearch(@RequestParam("subject") String subject, @RequestParam("desiredOutput") String desiredOutput) {
        List<GraphItem> items = expertService.findPath(subject, desiredOutput);
        return ResponseEntity.ok(CollectionModel.of(items));
    }
}
