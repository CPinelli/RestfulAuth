package com.mycompany.security.restfulauth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.security.restfulauth.config.SecurityConfig;
import com.mycompany.security.restfulauth.utils.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private SecurityConfig securityConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SecurityConfig securityConfig) {
        this.authenticationManager = authenticationManager;
        this.securityConfig = securityConfig;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {

        String username;
        String password;

        try {

            Map<String, String> credentials = new ObjectMapper().readValue(req.getInputStream(), Map.class);

            username = credentials.get("username");
            password = credentials.get("password");

            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                throw new RuntimeException("Request does not contain credentials");
            }

        } catch (IOException e) {
            LOGGER.error("Exception caught while trying to get parameters from login request");
            throw new RuntimeException("Request is not valid");
        }


        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + this.securityConfig.getExpiration()))
                .signWith(SignatureAlgorithm.HS512, SecurityUtils.SECRET.getBytes())
                .compact();
        res.addHeader(SecurityUtils.HEADER, token);
    }
}