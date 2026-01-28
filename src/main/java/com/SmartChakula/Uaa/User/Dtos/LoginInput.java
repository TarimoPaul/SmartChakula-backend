package com.SmartChakula.Uaa.User.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInput {
        private String identifier;
        private String password;

        // Explicit getters for GraphQL mapping
        public String getIdentifier() {
                return identifier;
        }

        public String getPassword() {
                return password;
        }
}