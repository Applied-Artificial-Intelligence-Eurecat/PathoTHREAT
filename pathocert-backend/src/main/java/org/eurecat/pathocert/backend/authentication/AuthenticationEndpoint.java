package org.eurecat.pathocert.backend.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import org.eurecat.pathocert.backend.authentication.jwt.JWTProvider;
import org.eurecat.pathocert.backend.users.model.User;
import org.eurecat.pathocert.backend.users.model.UserRole;
import org.eurecat.pathocert.backend.users.repository.OrganizationRepository;
import org.eurecat.pathocert.backend.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Map;

@RestController
public class AuthenticationEndpoint {

    @Autowired
    public UserRepository users;
    @Autowired
    public OrganizationRepository organizationRepository;
    @Autowired
    public JWTProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private WareService wareService;

    @PostMapping(path = "/api/authenticate")
    public ResponseEntity<EntityModel<AuthResponse>> authenticate(@RequestBody AuthenticateBody auth) {
        try {
            // Should normally check if the account is expired, but it is not a requirement.
            //var new_auth = new AuthResponse(jwtProvider.createToken(auth.getUsername(), auth.getPassword()));
            var new_auth = new AuthResponse(wareService.getTokenFromPathoWARE(auth.getUsername(), auth.getPassword()));
            var user_opt = users.findByUsername(auth.getUsername());
            if (user_opt.isEmpty()) {
                User user = new User();
                user.setUsername(auth.getUsername());
                // Password is encoded
                user.setPassword(auth.getPassword());
                user.setUserRole(UserRole.SUPER_ADMIN);
                var org = organizationRepository.findAll().iterator();
                if (org.hasNext()) {
                    user.setOrganization(org.next());
                }
                users.save(user);
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(auth.getUsername(), auth.getPassword()));
            var entity_m = EntityModel.of(new_auth);
            var selfLink = Link.of(UriTemplate.of("http://localhost:4567/api/organizations/" + 0 + "/"), "org");
            entity_m.add(selfLink);
            return ResponseEntity.ok(entity_m);
        } catch (Exception e) {
            // All the other are unable to launch.
            System.err.println("Bad Credentials: ");
            e.printStackTrace();
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @PostMapping("/api/authenticate/token")
    public ResponseEntity<Boolean> testTokenIsExpired(@RequestBody Map<String, String> input) {
        try {
            jwtProvider.getUsername(input.get("token"));
        } catch (ExpiredJwtException | IllegalArgumentException | AuthenticationException | JsonProcessingException e) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    private EntityModel<AuthResponse> addOrg(EntityModel<AuthResponse> model, User user) {
        final var org = user.getOrganization();
        if (org != null) {
            var selfLink = Link.of(UriTemplate.of("http://localhost:4567/api/organizations/" + org.getId() + "/"), "org");
            model.add(selfLink);
        } else {
            var selfLink = Link.of(UriTemplate.of("http://localhost:4567/api/organizations/" + 0 + "/"), "org");
            model.add(selfLink);
        }
        return model;
    }
}
