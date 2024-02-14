package org.eurecat.pathocert.backend.users.endpoint;

import org.eurecat.pathocert.backend.users.model.Organization;
import org.eurecat.pathocert.backend.users.repository.OrganizationRepository;
import org.eurecat.pathocert.backend.users.repository.UserRepository;
import org.eurecat.pathocert.backend.users.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@BasePathAwareController
@RequestMapping(value = "/api/organizations/")
public class OrganizationsEndpoint {

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserRepository userRepository;


    @PostMapping(path = "/my-organization")
    @ResponseBody
    public ResponseEntity<EntityModel<Organization>> getMyOrg() {
        final var principalInside = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var principal = (String) principalInside;
        var user_opt = userRepository.findByUsername((String) principal);
        if (user_opt.isPresent()) {
            var us = user_opt.get();
            final var org = Optional.ofNullable(us.getOrganization());
            return org
                    .map(org1 -> ResponseEntity.ok(EntityModel.of(org1)))
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

}
