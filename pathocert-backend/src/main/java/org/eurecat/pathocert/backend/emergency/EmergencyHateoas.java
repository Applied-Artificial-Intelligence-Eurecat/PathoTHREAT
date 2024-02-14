package org.eurecat.pathocert.backend.emergency;

import lombok.SneakyThrows;
import org.eurecat.pathocert.backend.emergency.endpoint.EmergenciesEndpoint;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelProcessor;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class EmergencyHateoas implements RepresentationModelProcessor<EntityModel<Emergency>> {

    @NotNull
    @SneakyThrows
    @Override
    public EntityModel<Emergency> process(EntityModel<Emergency> model) {
        /*
        Link archivedLink =
                linkTo(methodOn(EmergenciesEndpoint.class)
                        .getAuthorizedArchived(true)

                ).withSelfRel();
        model.add(archivedLink.withRel(LinkRelation.of("org-archived")));*/
        var selfLink = Link.of(UriTemplate.of("/api/emergencies/" +
                Objects.requireNonNull(model.getContent()).getId() + "/"), "self");
        model.add(selfLink);
        return model;
    }
}
