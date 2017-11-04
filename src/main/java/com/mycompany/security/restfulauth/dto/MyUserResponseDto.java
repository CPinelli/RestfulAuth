package com.mycompany.security.restfulauth.dto;

import com.mycompany.security.restfulauth.entities.MyUser;

import java.util.Date;

import lombok.Data;

@Data
public class MyUserResponseDto {

    private String username;

    private String email;

    private Date registerDate;

    public MyUserResponseDto() {

    }

    public MyUserResponseDto(MyUser myUser) {

        this.username = myUser.getUsername();
        this.email = myUser.getEmail();
        this.registerDate = myUser.getRegisterDate();

    }
}
