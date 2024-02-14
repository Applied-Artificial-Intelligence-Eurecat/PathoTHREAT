package org.eurecat.pathocert.backend.endpoints;

import com.jayway.jsonpath.JsonPath;
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

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("assessment-test")
public class EmergencyAssessmentTests {

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

    @DisplayName("When close-assessments successful")
    @Test
    void whenCloseAssessmentsEndpoint_responseIsSuccessful() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/assessment/1/close-assessments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @DisplayName("When merge documents successful")
    @Test
    void whenMergeDocumentsEndpoint_responseIsSuccessful() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/assessment/merge-documents")
                        .header("Authorization", "Bearer " + token)
                        .content("[\"Document Name 1\",\"Document Name 2\"]")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @DisplayName("When merge documents without body not successful")
    @Test
    void whenMergeDocumentsEndpointWithoutBody_responseIsNotSuccessful() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/assessment/merge-documents")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
}
