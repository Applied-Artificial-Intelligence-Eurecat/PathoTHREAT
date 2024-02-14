package org.eurecat.pathocert.backend.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WareService {

    public String getTokenFromPathoWARE(String username, String password) throws AuthenticationException, JsonProcessingException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("client_id", "pathothreat");
        body.put("grant_type", "password");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://idm.digital-enabler.eng.it/auth/realms/pathocert/protocol/openid-connect/token"))
               //.uri(URI.create("http://pathocert-kc:8080/realms/pathocert/protocol/openid-connect/token"))
                .method("POST", getParamsUrlEncoded(body))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assert response != null;
        if (response.statusCode() != 200) {
            throw new AuthenticationException();
        }
        Map<String, Object> result = new ObjectMapper().readValue(response.body(), HashMap.class);
        if (result.containsKey("access_token")) {
            return (String) result.get("access_token");
        } else {
            throw new AuthenticationException();
        }
    }

    public Map<String, Object> getInfoFromPathoWARE(String token) throws AuthenticationException, JsonProcessingException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://idm.digital-enabler.eng.it/auth/realms/pathocert/protocol/openid-connect/userinfo"))
                //.uri(URI.create("http://pathocert-kc:8080/realms/pathocert/protocol/openid-connect/userinfo"))
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .header("Authorization", "Bearer " + token)
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assert response != null;
        if (response.statusCode() != 200) {
            throw new AuthenticationException();
        }
        return new ObjectMapper().readValue(response.body(), HashMap.class);
    }

    public String getUsernameFromTokenWARE(String token) throws AuthenticationException, JsonProcessingException {
        var result = getInfoFromPathoWARE(token);
        if (result.containsKey("given_name")) {
            return (String) result.get("given_name");
        }
        if (result.containsKey("preferred_username")) {
            return (String) result.get("preferred_username");
        }
        throw new AuthenticationException();
    }

    public boolean getTokenIsValidWARE(String token) throws AuthenticationException, JsonProcessingException {
        var result = getInfoFromPathoWARE(token);
        return result.containsKey("given_name") || result.containsKey("preferred_username");
    }

    private HttpRequest.BodyPublisher getParamsUrlEncoded(Map<String, String> parameters) {
        String urlEncoded = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return HttpRequest.BodyPublishers.ofString(urlEncoded);
    }
}
