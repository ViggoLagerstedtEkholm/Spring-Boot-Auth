package com.example.javabackendcasemicrolabsaa.Models.DTO;

import lombok.Data;

@Data
public class UserResponse {
    private String username;
    private String name;
    private Long id;

    public UserResponse(String username, String name, Long id) {
        this.username = username;
        this.name = name;
        this.id = id;
    }
}
