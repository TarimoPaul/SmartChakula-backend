package com.SmartChakula.Utils;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphQlListResponse<T> {
    private String status;
    private String message;
    private List<T> data;
}
