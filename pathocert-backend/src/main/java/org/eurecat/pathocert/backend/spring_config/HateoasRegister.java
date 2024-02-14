package org.eurecat.pathocert.backend.spring_config;

import org.eurecat.pathocert.backend.authentication.hateoas.AuthResponseHateoas;
import org.eurecat.pathocert.backend.authentication.hateoas.AuthenticateBodyHateoas;
import org.eurecat.pathocert.backend.emergency.EmergencyHateoas;
import org.eurecat.pathocert.backend.users.UserHateoas;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HateoasRegister {

    @Bean
    EmergencyHateoas emergencyHateoasProcessor() {
        return new EmergencyHateoas();
    }

    @Bean
    UserHateoas userHateoasProcessor() {
        return new UserHateoas();
    }

    @Bean
    AuthResponseHateoas authResponseHateoas() {
        return new AuthResponseHateoas();
    }

    @Bean
    AuthenticateBodyHateoas authenticateBodyHateoas() {
        return new AuthenticateBodyHateoas();
    }
}
