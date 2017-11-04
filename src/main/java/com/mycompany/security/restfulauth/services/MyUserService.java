package com.mycompany.security.restfulauth.services;

import com.mycompany.security.restfulauth.dto.MyUserRequestDto;
import com.mycompany.security.restfulauth.entities.MyUser;
import com.mycompany.security.restfulauth.exceptions.AlreadyExistsException;
import com.mycompany.security.restfulauth.repositories.MyUserRepository;
import com.mycompany.security.restfulauth.utils.HashUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MyUserService {

    private MyUserRepository myUserRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MyUserService.class);

    @Autowired
    public MyUserService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    public MyUser create(MyUserRequestDto myUserRequestDto) throws AlreadyExistsException {

        LOGGER.info("Received signup request for user [{}] using email [{}]", myUserRequestDto.getUsername(),
                    myUserRequestDto.getEmail());

        MyUser myUser = this.myUserRepository.findByEmail(myUserRequestDto.getEmail());
        if (myUser != null) {
            LOGGER.error("The email address [{}] is already in use", myUserRequestDto.getEmail());
            throw new AlreadyExistsException("This email address is already in use");
        }
        myUser = this.myUserRepository.findByUsername(myUserRequestDto.getUsername());

        if (myUser != null) {
            LOGGER.error("The username [{}] is already in use", myUserRequestDto.getUsername());
            throw new AlreadyExistsException("This username is already in use");
        }

        myUser = new MyUser();

        myUser.setRegisterDate(new Date());
        myUser.setActive(false);
        myUser.setUsername(myUserRequestDto.getUsername());
        myUser.setEmail(myUserRequestDto.getEmail());
        myUser.setPassword(HashUtils.hash(myUserRequestDto.getPassword()));

        this.myUserRepository.save(myUser);

        LOGGER.info("User [{}] using email [{}] has been registered", myUserRequestDto.getUsername(),
                    myUserRequestDto.getEmail());

        return myUser;
    }

    public List<MyUser> getAllActiveUsers(String username) {

        LOGGER.info("Received request from user [{}] to get all active users", username);
        return this.myUserRepository.findAllByIsActiveIsTrue();
    }
}
