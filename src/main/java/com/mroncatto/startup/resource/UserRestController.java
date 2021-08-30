package com.mroncatto.startup.resource;

import com.mroncatto.startup.domain.HttpResponse;
import com.mroncatto.startup.domain.User;
import com.mroncatto.startup.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/v1")
@Tag(name = "Users", description = "Users endpoint resource")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {
    private final UserService userService;

    @Operation(summary = "Get all users", security = {
            @SecurityRequirement(name = "bearerAuth")}, responses = {
            @ApiResponse(description = "Successfully", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HttpResponse.class)))})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/user")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = this.userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


}
