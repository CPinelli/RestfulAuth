package com.mycompany.security.restfulauth.utils;

import io.jsonwebtoken.Jwts;

public class JwtUtils {

    public static String getUsername(String jwt) {

        return Jwts.parser()
                .setSigningKey(SecurityUtils.SECRET.getBytes())
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}
