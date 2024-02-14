package org.eurecat.pathocert.backend.authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticateBody {
    private String username, password;
}
