package com.example.javabackendcasemicrolabsaa;

import com.example.javabackendcasemicrolabsaa.Models.Persistance.Role;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.User;
import com.example.javabackendcasemicrolabsaa.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class JavaBackendCaseMicrolabsAaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBackendCaseMicrolabsAaApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService){
        return arg -> {
            userService.saveRole(new Role(null, "ROLE_STANDARD"));

            userService.saveUser(new User(null, "Email1", "name1", "username1", "field1", new ArrayList<>()));
            userService.saveUser(new User(null, "Email2", "name2", "username2", "field2", new ArrayList<>()));
            userService.saveUser(new User(null, "Email3", "name3", "username3", "field3", new ArrayList<>()));
            userService.saveUser(new User(null, "Email4", "name4", "username4", "field4", new ArrayList<>()));

            for(User user :userService.getAllUsers()) {
                userService.addRoleToUser(user.getUsername(), "ROLE_STANDARD");
            }
        };
    }
}
