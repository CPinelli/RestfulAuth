package com.mycompany.security.restfulauth.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashUtils {

    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(11);

    public static String hash(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }
}
