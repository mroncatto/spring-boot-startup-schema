package com.mroncatto.startup.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mroncatto.startup.domain.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletException;
import java.util.*;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.mroncatto.startup.constant.AppConstant.*;
import static com.mroncatto.startup.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    public String generateJwtToken(UserPrincipal user, boolean refresh) {
        Algorithm algorithm = HMAC256(secret.getBytes());
        return JWT.create()
                .withAudience(APP_NAME)
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + (refresh ? REFRESH_TOKEN_EXPIRES_IN : TOKEN_EXPIRES_IN) * 60 * 1000))
                .withIssuer(ServletUriComponentsBuilder.fromCurrentContextPath().path("").toUriString())
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public String getSubject(String token) {
        return getDecodedJwt(token).getSubject();
    }

    private DecodedJWT getDecodedJwt(String token) {
            Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
    }

    public Collection<SimpleGrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(claims).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return authorities;

    }

    public String[] getClaimsFromToken(String token) {
        return getDecodedJwt(token).getClaim("roles").asArray(String.class);
    }


}
