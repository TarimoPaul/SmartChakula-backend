package com.SmartChakula.Uaa.User.Controler;

import lombok.RequiredArgsConstructor;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Uaa.User.Dtos.AuthResponse;
import com.SmartChakula.Uaa.User.Dtos.LoginInput;
import com.SmartChakula.Uaa.User.Services.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @MutationMapping(name = "login")
    public AuthResponse login(@Argument LoginInput input) {
        return authService.login(input.identifier(), input.password());
    }
    
}
