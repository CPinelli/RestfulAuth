package com.mycompany.security.restfulauth.dto;

import lombok.Data;

@Data
public class MyUserRequestDto {

    private String username;

    private String password;

    private String email;

}
