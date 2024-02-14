package org.eurecat.pathocert.backend.users;

import org.eurecat.pathocert.backend.emergency.endpoint.EmergenciesEndpoint;
import org.eurecat.pathocert.backend.users.endpoint.OrganizationsEndpoint;
import org.eurecat.pathocert.backend.users.endpoint.UsersEndpoint;
import org.eurecat.pathocert.backend.users.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelProcessor;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UserHateoas implements RepresentationModelProcessor<EntityModel<User>> {

    @NotNull
    @Override
    public EntityModel<User> process(@NotNull EntityModel<User> model) {
        addArchivedLink(model);
        addMyOrg(model);
        addSelf(model);
        addOrg(model);
        return model;
    }

    private void addOrg(EntityModel<User> model) {
        var org = Objects.requireNonNull(model.getContent()).getOrganization();
        if (org != null) {
            var selfLink = Link.of(UriTemplate.of("http://localhost:4567/api/organizations/" +
                    org.getId() + "/"), "org");
            model.add(selfLink);
        }
    }

    private void addSelf(EntityModel<User> model) {
        var selfLink = Link.of(UriTemplate.of("http://localhost:4567/api/users/" +
                Objects.requireNonNull(model.getContent()).getId() + "/"), "self");
        model.add(selfLink);
    }

    private void addMyOrg(EntityModel<User> model) {
        var myOrgRel =
                linkTo(methodOn(OrganizationsEndpoint.class)
                        .getMyOrg()

                ).withRel("my-org");
        model.add(myOrgRel);
    }

    private void addArchivedLink(EntityModel<User> model) {
        /*
        var archivedLink =
                linkTo(methodOn(EmergenciesEndpoint.class)
                        .getAuthorizedArchived(true)

                ).withRel("org-archived");
        model.add(archivedLink);*/
    }
}