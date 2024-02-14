package org.eurecat.pathocert.backend.users.endpoint;

import org.eurecat.pathocert.backend.users.model.User;
import org.eurecat.pathocert.backend.users.repository.UserRepository;
import org.eurecat.pathocert.backend.users.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@BasePathAwareController
@RequestMapping(value = "/api/users/")
public class UsersEndpoint {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/my-user")
    @ResponseBody
    public ResponseEntity<EntityModel<User>> getMyUser() {
        try {
            final var principal_username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var user_opt = userRepository.findByUsername((String) principal_username);
            return user_opt.map(user -> ResponseEntity.ok(EntityModel.of(user))).orElseGet(() -> ResponseEntity.badRequest().build());

        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
