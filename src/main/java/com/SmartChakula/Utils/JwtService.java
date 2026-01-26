package com.SmartChakula.Utils;

/*
 * Simple JwtService implementation added here so the type is available.
 * This returns a generated UUID string as a token; replace with real JWT logic as needed.
 */
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.SmartChakula.Uaa.User.Entity.UserEntity;

@Service
public
class JwtService {
    public String generateToken(UserEntity user) {
        return UUID.randomUUID().toString();
    }
}
