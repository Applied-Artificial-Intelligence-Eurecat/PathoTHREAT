package org.eurecat.pathocert.backend.endpoints;

import com.jayway.jsonpath.JsonPath;
import org.eurecat.pathocert.backend.emergency.repository.EmergencyRepository;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentServiceInt;
import org.junit.jupiter.api.Assertions;
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
@ActiveProfiles("test")
public class EmergenciesEndpointTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    NeoDocumentServiceInt neoDocumentService;

    @Autowired
    EmergencyRepository emergencyRepository;

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

    @DisplayName("When sending to endpoint get emergencies")
    @Test
    void whenFirstGettingEmergencies() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/emergencies/my")
                        .param("archived", "false")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/hal+json"));
    }

    @DisplayName("When sending to endpoint get archived emergencies")
    @Test
    void whenGettingArchivedEmergencies() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/emergencies/my")
                        .param("archived", "true")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/hal+json"));
    }

    @DisplayName("Creating emergency adds a new emergency")
    @Test
    void whenCreateEmergency_OneMoreEmergency() throws Exception {
        long count = emergencyRepository.count();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/emergencies")
                        .header("Authorization", "Bearer " + token)
                        .content("{\"id\": 0, \"reportDate\": 0, \"reportingOrganization\": \"http://localhost:4567/api/organizations/1/\", \"reportingUserId\": \"http://localhost:4567/api/users/1/\"}"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Assertions.assertEquals(count+1, emergencyRepository.count());
    }

    @DisplayName("When getting selectable-values, get correct values")
    @Test
    void whenGettingSelectableValues_valuesAreCorrect() throws Exception {
        String expectedJson = "{\"symptoms\": {\"diarrhea\": \"diarrhea\", \"cramps\": \"cramps\", \"vomiting\": \"vomiting\"}," +
                "\"infrastructures\": {\"Large number of hospital patients\": \"Large number of hospital patients\"}," +
                "\"contaminants\": {\"ecoli\": \"ecoli\", \"norovirus\": \"norovirus\", \"rotavirus\": \"rotavirus\"}}";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/emergencies/selectable-values")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }
}
