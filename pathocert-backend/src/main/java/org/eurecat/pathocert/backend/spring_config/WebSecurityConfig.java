package org.eurecat.pathocert.backend.spring_config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.eurecat.pathocert.backend.authentication.jwt.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.ws.rs.HttpMethod;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("**")
                .permitAll()
                /*
                .authorizeRequests()
                .antMatchers("/api/authenticate")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/api/users/search/findByUsername**")
                .hasRole("SUPER_ADMIN")
                .and()
                .authorizeRequests()
                .antMatchers("/v3/api-docs**")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/swagger-ui**")
                .permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                 */
                .and()
                .addFilterBefore(jwtFilterBean(), UsernamePasswordAuthenticationFilter.class)
        ;

    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JWTFilter jwtFilterBean() {
        return new JWTFilter();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().components(new Components().addSecuritySchemes("jwt",
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT authentication for client.")));
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedMethods(HttpMethod.GET, HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.POST);
    }
}
