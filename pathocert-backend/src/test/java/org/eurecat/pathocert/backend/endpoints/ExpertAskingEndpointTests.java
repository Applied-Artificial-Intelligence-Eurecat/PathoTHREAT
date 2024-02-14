package org.eurecat.pathocert.backend.endpoints;

import com.jayway.jsonpath.JsonPath;
import org.eurecat.pathocert.backend.emergency.repository.EmergencyRepository;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentServiceInt;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class ExpertAskingEndpointTests {

    @Autowired
    MockMvc mockMvc;

    String token;

    @BeforeEach
    void beforeEach() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticate").content("{\"username\": \"pathothreat_user_test\", \"password\": \"sfer3\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/hal+json"))
                .andReturn();

        token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @DisplayName("When searching a correct value is returned")
    @Test
    void whenSearchingWithCorrectParameters_isSuccessful() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/expert/search-terms")
                        .header("Authorization", "Bearer " + token)
                        .param("subject", "ecoli")
                        .param("desiredOutput", "Event"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @DisplayName("When searching with params missing return error")
    @Test
    void whenSearchingWithMissingParameters_isNotSuccessful() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/expert/search-terms")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @DisplayName("Values returns the ordered list of values from the DB")
    @Test
    void whenCallingValues_returnsOrderedList() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/expert/values")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
        net.minidev.json.JSONArray listString = JsonPath.read(result.getResponse().getContentAsString(), "$._embedded.strings");
        System.out.println(listString.toString());
        List<String> givenResult = List.of(listString.toString().substring(1, listString.toString().length() - 1).split(","));
        assertEquals(givenResult.stream().sorted().collect(Collectors.toList()), givenResult);
    }

    @DisplayName("Labels returns an ordered list of labels")
    @Test
    void whenCallingLabels_returnsOrderedList() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/expert/labels")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
        net.minidev.json.JSONArray listString = JsonPath.read(result.getResponse().getContentAsString(), "$._embedded.strings");
        System.out.println(listString.toString());
        List<String> givenResult = List.of(listString.toString().substring(1, listString.toString().length() - 1).split(","));
        assertEquals(givenResult.stream().sorted().collect(Collectors.toList()), givenResult);
    }

    @DisplayName("Values/type returns list of values")
    @Test
    void whenCallingValuesOfType_returnsOrderedList() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/expert/values/Event")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @DisplayName("Labels/subject returns a valid result")
    @Test
    void whenCallingLabelsOfSubject_returnsOrderedList() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/expert/labels/ecoli")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}
