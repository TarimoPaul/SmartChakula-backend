package com.SmartChakula.Uaa.Config;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RootUserInitializer implements ApplicationRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        boolean rootExists = userRepo.existsByEmail(RootUserConfig.ROOT_EMAIL);

        if (!rootExists) {
            UserEntity rootUser = new UserEntity();
            rootUser.setFullName(RootUserConfig.ROOT_FULL_NAME);
            rootUser.setEmail(RootUserConfig.ROOT_EMAIL);
            rootUser.setPassword(passwordEncoder.encode(RootUserConfig.ROOT_PASSWORD));
            rootUser.setPhone(RootUserConfig.ROOT_PHONE);
            rootUser.setRole(UserRole.ADMIN);
            rootUser.setIsActive(true);
            rootUser.setCreatedAt(LocalDateTime.now());

            userRepo.save(rootUser);

            System.out.println("Root user created with email: " + RootUserConfig.ROOT_EMAIL);
        } else {
            System.out.println("Root user already exists. Skipping creation.");
        }

    }

}
