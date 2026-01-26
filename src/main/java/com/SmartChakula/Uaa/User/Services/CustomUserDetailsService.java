package com.SmartChakula.Uaa.User.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Repository.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepo
                .findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .disabled(!user.getIsActive())
                .build();
    }
}