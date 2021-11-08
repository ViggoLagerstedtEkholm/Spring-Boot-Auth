package com.example.javabackendcasemicrolabsaa.Repositories;

import com.example.javabackendcasemicrolabsaa.Models.Persistance.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
