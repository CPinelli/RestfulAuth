package com.mycompany.security.restfulauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class SecurityConfig {

    @Value("${spring.security.uri.signup}")
    private String signupUri;

    @Value("${spring.security.uri.login}")
    private String loginUri;

    @Value("${spring.security.jwt.expiration}")
    private Long expiration;

    @Value("${spring.security.bcrypt.strength}")
    private int strength;
}
