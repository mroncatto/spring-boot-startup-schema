package com.mroncatto.startup.resource;

import com.mroncatto.startup.domain.HttpResponse;
import com.mroncatto.startup.domain.User;
import com.mroncatto.startup.domain.UserPrincipal;
import com.mroncatto.startup.service.RoleService;
import com.mroncatto.startup.service.UserService;
import com.mroncatto.startup.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static com.mroncatto.startup.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping("/v1")
@Tag(name = "Users", description = "Users endpoint resource")
public class UserRestController {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserRestController(UserService userService, RoleService roleService, AuthenticationManager authenticationManager,
                              JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "Log in and get a token", responses = {
            @ApiResponse(description = "Successfully", responseCode = "200", headers = @Header(name = "Jwt-Token", description = "Json Web Token: Bearer",
                    schema = @Schema(implementation = String.class)), content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = HttpResponse.class)))) })
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/user/login")
    public ResponseEntity<User> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        authenticate(username, password);
        User loginUser = userService.findUserByUsername(username);
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }

    // Utilities -------------------------------------------------------------------------------------------------------

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

    }


}
