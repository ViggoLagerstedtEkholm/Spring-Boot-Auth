package com.example.javabackendcasemicrolabsaa.Models.DTO;

import lombok.Data;

@Data
public class ResetPassword {
    private String token;
    private String password;
}
