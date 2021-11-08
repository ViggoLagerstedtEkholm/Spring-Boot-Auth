package com.example.javabackendcasemicrolabsaa.API;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.javabackendcasemicrolabsaa.Helpers.JWTHelper;
import com.example.javabackendcasemicrolabsaa.Models.DTO.RefreshToken;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.Role;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.User;
import com.example.javabackendcasemicrolabsaa.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenController {
    private final UserService userService;

    @PostMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response, @RequestBody RefreshToken refresh) throws IOException {
        if(refresh.getRefreshToken() != null){
            try{
                //Verify the validity of the token.
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifierJWT = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifierJWT.verify(refresh.getRefreshToken());

                //Get the claim.
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                //Generate refresh token.
                String accessToken = JWTHelper.generateJWTTokenWithRoles(username, user.getRoles(), request,algorithm,45000 );

                //Send the response.
                JWTHelper.setTokens(response, accessToken, refresh.getRefreshToken());
            }catch(Exception e){
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
        else{
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
