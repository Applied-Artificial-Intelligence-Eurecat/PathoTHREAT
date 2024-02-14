package org.eurecat.pathocert.backend.endpoints;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
@SpringBootTest
public class AuthenticationEndpointTests {

    @Autowired
    MockMvc mockMvc;

    @DisplayName("Correct credentials return OK")
    @Test
    void whenAuthenticateWithCorrectCredentials_tokenIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticate").content("{\"username\": \"pathothreat_user_test\", \"password\": \"sfer3\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/hal+json"));
    }

    @DisplayName("Incorrect credentials don't return OK")
    @Test
    void whenAuthenticateWithIncorrectCredentials_NoTokenIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticate").content("{\"username\": \"pathothreat_user\", \"password\": \"sfer3\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @DisplayName("When verifying correct token return OK")
    @Test
    void whenVerifyingWithCorrectToken_returnsOK() throws Exception {
        // Get token
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticate").content("{\"username\": \"pathothreat_user_test\", \"password\": \"sfer3\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

        //Test token
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticate/token").content("{\"token\": \"" + token + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("false"))
                .andReturn();
    }

    @DisplayName("When verifying incorrect token don't return OK")
    @Test
    void whenVerifyingWithIncorrectToken_returnsNotOK() throws Exception {
        //Test token
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticate/token").content("{\"token\": \"not a token\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andReturn();
    }
}
