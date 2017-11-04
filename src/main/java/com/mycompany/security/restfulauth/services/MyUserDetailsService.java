package com.mycompany.security.restfulauth.services;

import com.mycompany.security.restfulauth.entities.MyUser;
import com.mycompany.security.restfulauth.repositories.MyUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private MyUserRepository myUserRepository;

    @Autowired
    public MyUserDetailsService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser myUser = myUserRepository.findByUsername(username);
        if (myUser == null) {
            throw new UsernameNotFoundException(username);
        }
        if (!myUser.isActive()) {
            throw new UsernameNotFoundException("This account is not active, please check your email first");
        }
        return new org.springframework.security.core.userdetails.User(myUser.getUsername(), myUser.getPassword(),
                                                                      Collections.emptyList());
    }
}
