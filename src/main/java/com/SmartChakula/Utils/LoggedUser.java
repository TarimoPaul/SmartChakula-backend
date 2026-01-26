package com.SmartChakula.Utils;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Repository.UserRepo;

import lombok.extern.java.Log;

@Log
public class LoggedUser {

    /**
     * Pata email ya user aliyeingia
     */
    public static String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return "unknown@bauu.com";
        }


        // Kama ni username tu
        if (auth.getPrincipal() instanceof String) {
            return auth.getPrincipal().toString();
        }

        return "unknown@babuu.me";
    }

    /**
     * Pata jina la user aliyeingia
     */
    public static String getName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return "System User";
        }

        return "Unknown User";
    }

    /**
     * Pata UID ya user aliyeingia
     */
    public static String getUid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return "system-user";
        }

        return "system-user";
    }

    /**
     * Pata User object kutoka database
     */
    public static UserEntity getUser() {
        try {
            UserRepo userRepository = SpringContext.getBean(UserRepo.class);
            Optional<UserEntity> user = userRepository.findByEmail(getUid());
            return user.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Andika log message na jina la user
     */
    public static void logUserAction(String action) {
        // Removed logging
    }

    /**
     * Check kama user ameingia
     */
    public static boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() != null;
    }

    /**
     * Pata taarifa kamili za user kwa urahisi
     */
    public static String getUserInfo() {
        if (!isLoggedIn()) {
            return "Hakuna user aliyeingia";
        }
        return "Jina: " + getName() + ", Email: " + getEmail();
    }
}