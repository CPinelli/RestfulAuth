package com.mycompany.security.restfulauth.controllers;

import com.mycompany.security.restfulauth.dto.MyUserRequestDto;
import com.mycompany.security.restfulauth.dto.MyUserResponseDto;
import com.mycompany.security.restfulauth.entities.MyUser;
import com.mycompany.security.restfulauth.exceptions.AlreadyExistsException;
import com.mycompany.security.restfulauth.exceptions.UnauthorizedException;
import com.mycompany.security.restfulauth.services.MyUserService;
import com.mycompany.security.restfulauth.utils.JwtUtils;
import com.mycompany.security.restfulauth.utils.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private MyUserService myUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(MyUserService myUserService) {
        this.myUserService = myUserService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public MyUserResponseDto createUser(@RequestBody MyUserRequestDto myUserRequestDto) throws AlreadyExistsException {

        MyUser myUser = this.myUserService.create(myUserRequestDto);
        return new MyUserResponseDto(myUser);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MyUserResponseDto> getUsers(@RequestHeader(value = SecurityUtils.HEADER) String header)
            throws UnauthorizedException {

        String username = JwtUtils.getUsername(header);

        // this should never happen
        if (StringUtils.isEmpty(username)) {
            LOGGER.error("An anonymous user requested to get all users");
            throw new UnauthorizedException("You must be logged in");
        }

        List<MyUser> myUsers = this.myUserService.getAllActiveUsers(username);

        return myUsers.stream().map(MyUserResponseDto::new).collect(Collectors.toList());
    }
}
