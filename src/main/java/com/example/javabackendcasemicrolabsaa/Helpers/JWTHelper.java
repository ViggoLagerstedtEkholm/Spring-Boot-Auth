package com.example.javabackendcasemicrolabsaa.Helpers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.javabackendcasemicrolabsaa.Models.DTO.RefreshToken;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
public class JWTHelper {
    public static String getToken( HttpServletRequest request ) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if ( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public static String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey("secret".getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static String generateJWTTokenWithGrantedAuthority(String username, Collection<GrantedAuthority> roles, HttpServletRequest request, Algorithm algorithm, int expiry ){
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiry ))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public static String generateJWTTokenWithRoles(String username, Collection<Role> roles, HttpServletRequest request, Algorithm algorithm, int expiry ){
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiry ))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles.stream().map(Role::getName).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public static void setTokens(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(OK.value());
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
