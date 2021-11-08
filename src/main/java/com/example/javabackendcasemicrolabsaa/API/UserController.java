package com.example.javabackendcasemicrolabsaa.API;

import com.example.javabackendcasemicrolabsaa.Helpers.FilterHelper;
import com.example.javabackendcasemicrolabsaa.Helpers.JWTHelper;
import com.example.javabackendcasemicrolabsaa.Models.DTO.*;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.User;
import com.example.javabackendcasemicrolabsaa.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController{
    private final UserService userService;

    @GetMapping("/user/all")
    public ResponseEntity<Pagination> getUsers(@RequestParam("page") int page,
                                               @RequestParam("resultsPerPage") int resultsPerPage){

        //Get all users.
        List<User> users = userService.getAllUsers();
        int count = users.size();

        return ResponseEntity.ok().body(FilterHelper.getPaginationResult(getUserResponse(users), count, page, resultsPerPage));
    }

    @GetMapping("/user/search")
    public ResponseEntity<Pagination> getUsersByUsername(@RequestParam("page") int page,
                                                         @RequestParam("resultsPerPage") int resultsPerPage,
                                                         @RequestParam("username") String username){
        //Get user with a specific username.
        List<User> users = userService.getAllUsersByUsername(username);
        int count = users.size();

        return ResponseEntity.ok().body(FilterHelper.getPaginationResult(getUserResponse(users), count, page, resultsPerPage));
    }

    private List<UserResponse> getUserResponse(List<User> users){
        List<UserResponse> userResponses = new ArrayList<>();

        for (User user: users) {
            userResponses.add(new UserResponse(user.getUsername(), user.getName(), user.getId()));
        }
        return userResponses;
    }

    @PostMapping("/user/register")
    public ResponseEntity<User> registerUser(Register register){
        log.info("Registering user!");

        User existingUserCheckUsername = userService.getUser(register.getUsername());
        User existingUserCheckEmail = userService.findUserByEmail(register.getEmail());

        //Check if there exists a user with this username.
        if(existingUserCheckUsername == null && existingUserCheckEmail == null){
            User registerUser = new User();
            registerUser.setId(null);
            registerUser.setUsername(register.getUsername());
            registerUser.setName(register.getName());
            registerUser.setPassword(register.getPassword());
            registerUser.setEmail(register.getEmail());

            userService.saveUser(registerUser);
            userService.addRoleToUser(registerUser.getUsername(), "ROLE_STANDARD");

            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/register").toUriString());
            return ResponseEntity.created(uri).build();
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/user/update/password")
    public ResponseEntity<?> updatePassword(HttpServletRequest request, Password password){
        String token = JWTHelper.getToken(request);
        String username = JWTHelper.getUsernameFromToken(token);

        User modifyUser = userService.getUser(username);
        String storedHashPass = modifyUser.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(password.getCurrentPass(), storedHashPass);

        //Check if the current password matches with the provided one.
        if(matches){
            modifyUser.setPassword(password.getNewPass());
            userService.saveUser(modifyUser);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/user/remove")
    public ResponseEntity<?> removeUser(@RequestBody RemoveUser removeRequest){
        User user = userService.getUser(removeRequest.getUsername());
        userService.deleteUser(user);
        return ResponseEntity.ok().build();
    }
}
