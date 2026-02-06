package com.SmartChakula.Uaa.User.Controler;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Uaa.User.Dtos.UserDto;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Services.UserAdminService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @QueryMapping
    public List<UserDto> getAllUsers() {
        return userAdminService.getAllUsers();
    }

    @MutationMapping
    public UserDto updateUserRole(@Argument String uid, @Argument UserRole role) {
        return userAdminService.updateUserRole(uid, role);
    }

    @MutationMapping
    public UserDto changeUserPassword(@Argument String uid, @Argument String newPassword) {
        return userAdminService.changeUserPassword(uid, newPassword);
    }
}
