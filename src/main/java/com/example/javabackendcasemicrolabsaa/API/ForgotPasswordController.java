package com.example.javabackendcasemicrolabsaa.API;

import com.example.javabackendcasemicrolabsaa.Exceptions.UserNotFoundException;
import com.example.javabackendcasemicrolabsaa.Models.DTO.GenericResponse;
import com.example.javabackendcasemicrolabsaa.Models.DTO.ResetPassword;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.User;
import com.example.javabackendcasemicrolabsaa.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordController {
    private final UserService userService;

    private final JavaMailSender mailSender;

    @PostMapping("/reset/password")
    public GenericResponse resetPassword(@RequestParam("email") String email) throws UserNotFoundException {
        log.info("Email: {}", email);
        User user = userService.findUserByEmail(email);

        if (user == null) {
            throw new UserNotFoundException();
        }

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        mailSender.send(constructResetTokenEmail("https://viggolagerstedtekholm.github.io/microlabscasebackend", token, user));

        return new GenericResponse("Sent reset password link to, " + email);
    }

    @GetMapping("/reset/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        String resetToken = userService.validatePasswordResetToken(token);
        if(resetToken != null){
            return ResponseEntity.badRequest().body("NOT A VALID TOKEN");
        }else{
            return ResponseEntity.ok().body("OK");
        }
    }

    @PostMapping("/reset/update")
    public ResponseEntity<?> updateNewPassword(ResetPassword resetPassword){
        log.info("Token val, {}", resetPassword.getToken());
        String resetToken = userService.validatePasswordResetToken(resetPassword.getToken());

        if(resetToken != null){
            ResponseEntity.badRequest().body("NOT A VALID TOKEN");
        }

        User modifyUser = userService.getUserByPasswordResetToken(resetPassword.getToken());

        if(modifyUser != null) {
            modifyUser.setPassword(resetPassword.getPassword());
            userService.saveUser(modifyUser);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().body("Token does not belong to user.");
        }
    }

    private SimpleMailMessage constructResetTokenEmail(String contextPath, String token, User user) {
        String url = contextPath + "/#/reset/password/" + token;
        return constructEmail("Reset Password", "You requested to change password, click this link:" + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom("unishare.validation@gmail.com");
        return email;
    }


}
