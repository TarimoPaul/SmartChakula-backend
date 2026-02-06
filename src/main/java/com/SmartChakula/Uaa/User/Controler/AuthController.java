package com.SmartChakula.Uaa.User.Controler;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Uaa.User.Dtos.AuthResponse;
import com.SmartChakula.Uaa.User.Dtos.LoginInput;
import com.SmartChakula.Uaa.User.Dtos.RegisterInput;
import com.SmartChakula.Uaa.User.Dtos.SaveOwnerInput;
import com.SmartChakula.Uaa.User.Dtos.SaveManagerInput;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Services.AuthService;
import com.SmartChakula.Utils.GraphQlResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @MutationMapping
    public GraphQlResponse<AuthResponse> login(@Argument LoginInput input) {
        log.info("login MUTATION CALLED with identifier: {}", input.getIdentifier());
        return authService.login(input.getIdentifier(), input.getPassword());
    }

    @MutationMapping
    public GraphQlResponse<AuthResponse> register(@Argument RegisterInput input) {
        log.info("register MUTATION CALLED for email: {}", input.getEmail());
        return authService.registerUser(
                input.getFullName(),
                input.getEmail(),
                input.getPassword(),
                input.getPhone(),
                input.getRole());
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public GraphQlResponse<AuthResponse> saveOwner(@Argument("input") SaveOwnerInput input) {
        log.info("saveOwner MUTATION CALLED for email: {}", input.getEmail());
        return authService.saveOwner(
                input.getFullName(),
                input.getEmail(),
                input.getPassword(),
                input.getPhone()

        );
    }

    @MutationMapping
    @PreAuthorize("hasRole('OWNER')")
    public GraphQlResponse<UserEntity> saveManager(@Argument("input") SaveManagerInput input,
            Authentication authentication) {
        String ownerEmail = authentication.getName();
        log.info("saveManager MUTATION CALLED for email: {}", input.getEmail());

        return authService.saveManager(
                input.getFullName(),
                input.getEmail(),
                input.getPassword(),
                input.getPhone(),
                ownerEmail,
                input.getRestaurantUids());
    }

}