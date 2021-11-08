package com.example.javabackendcasemicrolabsaa.Repositories;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String username);
}
