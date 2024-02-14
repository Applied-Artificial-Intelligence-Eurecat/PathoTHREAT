package org.eurecat.pathocert.backend.users.model;

import org.springframework.security.core.GrantedAuthority;

public interface GrantedUser extends GrantedAuthority {

    User getUser();

    @Override
    default String getAuthority() {
        return "ROLE_" + getUser().getUserRole().toString();
    }
}
