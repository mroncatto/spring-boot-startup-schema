package com.mroncatto.startup.resource;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mroncatto.startup.domain.HttpResponse;
import com.mroncatto.startup.domain.User;
import com.mroncatto.startup.domain.UserPrincipal;
import com.mroncatto.startup.service.UserService;
import com.mroncatto.startup.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mroncatto.startup.constant.SecurityConstant.TOKEN_CANNOT_BE_VERIFIED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Refresh token authentication")
public class RefreshTokenController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Renew token authentication", responses = {
            @ApiResponse(description = "successfully", responseCode = "200", headers = @Header(name = "Jwt-Token", description = "Json Web Token: Bearer", schema = @Schema(implementation = String.class)), content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HttpResponse.class)))) })
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/auth/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                String username = this.jwtTokenProvider.getSubject(refresh_token);
                User user = userService.findUserByUsername(username);
                UserPrincipal userPrincipal = new UserPrincipal(user);
                final String access_token = this.jwtTokenProvider.generateJwtToken(userPrincipal, false);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                tokens.put("token_type", "bearer");
                tokens.put("expires_at", JWT.decode(access_token).getExpiresAt().toString());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                log.error("Error logging in: {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", TOKEN_CANNOT_BE_VERIFIED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

}
