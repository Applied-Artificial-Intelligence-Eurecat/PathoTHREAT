package org.eurecat.pathocert.backend.authentication.hateoas;

import lombok.SneakyThrows;
import org.eurecat.pathocert.backend.authentication.AuthenticateBody;
import org.eurecat.pathocert.backend.emergency.endpoint.EmergenciesEndpoint;
import org.eurecat.pathocert.backend.users.endpoint.UsersEndpoint;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.RepresentationModelProcessor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AuthenticateBodyHateoas implements RepresentationModelProcessor<EntityModel<AuthenticateBody>> {

    @NotNull
    @SneakyThrows
    @Override
    public EntityModel<AuthenticateBody> process(EntityModel<AuthenticateBody> model) {
        /*
        Link archivedLink =
                linkTo(methodOn(EmergenciesEndpoint.class)
                        .getAuthorizedArchived(true)

                ).withSelfRel();*/
        var myUser = linkTo(methodOn(UsersEndpoint.class).getMyUser()).withSelfRel();
        // model.add(archivedLink.withRel(LinkRelation.of("org-archived")));
        model.add(myUser.withRel(LinkRelation.of("my-user")));
        return model;
    }


}
