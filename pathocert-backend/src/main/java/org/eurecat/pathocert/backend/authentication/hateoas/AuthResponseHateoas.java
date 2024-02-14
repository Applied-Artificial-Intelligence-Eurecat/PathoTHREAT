package org.eurecat.pathocert.backend.authentication.hateoas;

import lombok.SneakyThrows;
import org.eurecat.pathocert.backend.authentication.AuthResponse;
import org.eurecat.pathocert.backend.emergency.endpoint.EmergenciesEndpoint;
import org.eurecat.pathocert.backend.users.endpoint.OrganizationsEndpoint;
import org.eurecat.pathocert.backend.users.endpoint.UsersEndpoint;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.RepresentationModelProcessor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AuthResponseHateoas implements RepresentationModelProcessor<EntityModel<AuthResponse>> {

    @NotNull
    @SneakyThrows
    @Override
    public EntityModel<AuthResponse> process(@NotNull EntityModel<AuthResponse> model) {
        addArchivedAndMyUser(model);
        addMyOrg(model);
        return model;
    }

    private void addArchivedAndMyUser(EntityModel<AuthResponse> model) {
        /*
        Link archivedLink =
                linkTo(methodOn(EmergenciesEndpoint.class)
                        .getAuthorizedArchived(true)

                ).withSelfRel();*/
        var myUser = linkTo(methodOn(UsersEndpoint.class).getMyUser()).withSelfRel();
        //model.add(archivedLink.withRel(LinkRelation.of("org-archived")));
        model.add(myUser.withRel(LinkRelation.of("my-user")));
    }


    private void addMyOrg(EntityModel<AuthResponse> model) {
        var myOrgRel =
                linkTo(methodOn(OrganizationsEndpoint.class)
                        .getMyOrg()

                ).withRel("my-org");
        model.add(myOrgRel);
    }
}
