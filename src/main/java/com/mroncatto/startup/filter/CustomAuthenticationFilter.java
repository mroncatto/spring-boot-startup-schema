package com.mroncatto.startup.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mroncatto.startup.constant.AppConstant;
import com.mroncatto.startup.domain.HttpResponse;
import com.mroncatto.startup.domain.UserPrincipal;
import com.mroncatto.startup.repository.UserRepository;
import com.mroncatto.startup.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.mroncatto.startup.constant.AppConstant.APP_NAME;
import static com.mroncatto.startup.constant.SecurityConstant.EXPIRATION_TIME;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public static final String BAD_CREDENTIALS_MSG = "Bad Credentials";
    public static final String DISABLED_ACCOUNT = "Disabled Account";
    public static final String DISABLED_ACCOUNT_MSG = "Your account is disabled. If you think that is a error, contact the administrator.";
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final String secret;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserPrincipal user = (UserPrincipal) authResult.getPrincipal();
        this.loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        Algorithm algorithm = HMAC256(secret.getBytes());
        String access_token = JWT.create()
                .withAudience(APP_NAME)
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withAudience(APP_NAME)
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME * 2 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        tokens.put("token_type", "bearer");
        tokens.put("expires_at", JWT.decode(access_token).getExpiresAt().toString());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String username = request.getParameter("username");
        if (username != null)
            this.loginAttemptService.addUserToLoginAttemptCache(username);

        HttpResponse res = new HttpResponse(403, HttpStatus.FORBIDDEN, failed.getMessage().toUpperCase() ,failed.getMessage().toUpperCase());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(403);
        new ObjectMapper().writeValue(response.getOutputStream(), res);
        //super.unsuccessfulAuthentication(request, response, failed);
    }


}
