package com.SmartChakula.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphQlResponse<T> {
    private String status;      // "Success", "Error", "Failure"
    private String message;     // Response message
    private T data;             // Generic data - MenuItem, Category, Restaurant, User, etc
}
