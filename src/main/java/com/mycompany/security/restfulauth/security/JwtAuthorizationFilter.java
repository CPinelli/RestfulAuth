package com.mycompany.security.restfulauth.security;

import com.mycompany.security.restfulauth.config.SecurityConfig;
import com.mycompany.security.restfulauth.utils.SecurityUtils;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private SecurityConfig securityConfig;

    public JwtAuthorizationFilter(AuthenticationManager authManager, SecurityConfig securityConfig) {
        super(authManager);
        this.securityConfig = securityConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(SecurityUtils.HEADER);

        if (StringUtils.isEmpty(header)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws IOException {
        String token = request.getHeader(SecurityUtils.HEADER);
        if (token != null) {
            String user = "";
            try {
                user = Jwts.parser()
                        .setSigningKey(SecurityUtils.SECRET.getBytes())
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
            } catch (SignatureException e) {
                throw new IOException("The token signature is not valid, please try to login again");
            }
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}