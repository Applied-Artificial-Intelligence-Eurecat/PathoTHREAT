package org.eurecat.pathocert.backend.authentication.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import org.eurecat.pathocert.backend.users.service.UserDetailsImpl;
import org.eurecat.pathocert.backend.users.service.UserDetailsServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private UserDetailsServiceImpl userDetails;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            try {
                var token = processJwt(authorizationHeader.substring(7));
                token.ifPresent(t -> {
                    t.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(t);
                });
            } catch (InvalidJwtAuthenticationException e) {
                throw new IOException("Invalid Jwt authentication Exception");
            } catch (ExpiredJwtException e) {
                System.out.println("AUTHENTICATION WITH EXPIRED TOKEN");
            } catch (AuthenticationException e) {
                throw new RuntimeException(e);
            }
        }
        filterChain.doFilter(request, response);

    }

    private Optional<UsernamePasswordAuthenticationToken> processJwt(String jwt) throws InvalidJwtAuthenticationException, AuthenticationException, JsonProcessingException {
        String username;
        try {
            username = jwtProvider.getUsername(jwt);
        } catch (ExpiredJwtException | AuthenticationException | JsonProcessingException e) {
            return Optional.empty();
        }
        return getUsernamePasswordAuthenticationToken(jwt, username);
    }

    @NotNull
    private Optional<UsernamePasswordAuthenticationToken> getUsernamePasswordAuthenticationToken(String jwt, String username) throws InvalidJwtAuthenticationException, AuthenticationException, JsonProcessingException {
        //var userDetails = this.userDetails.loadUserByUsername(username);
        if (jwtProvider.validateToken(jwt)) {
            return Optional.of(new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    new ArrayList<>(Collections.singletonList(new GrantedAuthority() {
                        @Override
                        public String getAuthority() {
                            return "ROLE_SUPER_ADMIN";
                        }
                    }))
            ));
        }
        return Optional.empty();
    }

    private boolean isUserAuthenticatedTheSame(String username) {
        var b = false;
        final var context = SecurityContextHolder.getContext();
        final var authentication = context.getAuthentication();
        if (authentication != null) {
            final UserDetailsImpl principal;
            principal = (UserDetailsImpl) authentication.getPrincipal();
            final var user = principal.getUser();
            b = !Objects.equals(user.getUsername(), username);
        }
        return b;
    }
}
