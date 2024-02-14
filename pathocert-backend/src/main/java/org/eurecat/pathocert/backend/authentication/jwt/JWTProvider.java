package org.eurecat.pathocert.backend.authentication.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.eurecat.pathocert.backend.authentication.WareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JWTProvider {

    @Value("${security.jwt.token.expire-length:3600000}")
    private final long validityInMilliseconds = 3600000 * 12; // 12h
    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private WareService wareService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /*
    public String createToken(String username, List<String> roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }*/

    public String createToken(String username, String password) throws AuthenticationException, JsonProcessingException {
        var token = wareService.getTokenFromPathoWARE(username, password);
        return token;
    }


    public Authentication getAuthentication(String token) throws AuthenticationException, JsonProcessingException {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /*
    public String getUsername(String token) {
        // ExpiredJWTException al fer aixo
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }*/

    public String getUsername(String token) throws AuthenticationException, JsonProcessingException {
        return wareService.getUsernameFromTokenWARE(token);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) throws InvalidJwtAuthenticationException, AuthenticationException, JsonProcessingException {
        return wareService.getTokenIsValidWARE(token);
        /*
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException();
        }
         */
    }

}

