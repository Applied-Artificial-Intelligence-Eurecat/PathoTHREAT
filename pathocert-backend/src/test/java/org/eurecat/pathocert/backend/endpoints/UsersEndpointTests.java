package org.eurecat.pathocert.backend.endpoints;

import com.jayway.jsonpath.JsonPath;
import org.eurecat.pathocert.backend.users.model.User;
import org.eurecat.pathocert.backend.users.repository.UserRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
@SpringBootTest
public class UsersEndpointTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    User testUser;
    String token;

    @BeforeEach
    void beforeEach () throws Exception {
        testUser = userRepository.findByUsername("pathothreat_user_test").get();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/authenticate").content("{\"username\": \"pathothreat_user_test\", \"password\": \"sfer3\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @DisplayName("Get my user correct")
    @Test
    void whenGetUserWithToken_getReturnUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/my-user")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/hal+json"));
    }

    @DisplayName("Get my user incorrect")
    @Test
    void whenGetUserWithoutToken_dontReturnUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/my-user")
                        .header("Authorization", "Bearer not a token"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
