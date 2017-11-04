package com.mycompany.security.restfulauth.repositories;

import com.mycompany.security.restfulauth.entities.MyUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@PropertySource("application.properties")
@SpringBootTest
public class MyUserRepositoryTest {

    @Autowired
    private MyUserRepository myUserRepository;

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String EMAIL = "EMAIL@EMAIL.COM";

    @Before
    public void setUp() {
        MyUser myUser = this.myUserRepository.findByUsername(USERNAME);

        if (myUser == null) {

            myUser = new MyUser();
            myUser.setUsername(USERNAME);
            myUser.setPassword(PASSWORD);
            myUser.setEmail(EMAIL);

            this.myUserRepository.save(myUser);
        }
    }

    @Test
    public void getUser() {

        MyUser myUser = this.myUserRepository.findByUsername(USERNAME);

        assertThat(myUser).isNotNull();
        assertThat(myUser.getEmail()).isNotNull().isNotEmpty().isEqualTo(EMAIL);
        assertThat(myUser.getUsername()).isNotNull().isNotEmpty().isEqualTo(USERNAME);
        assertThat(myUser.getPassword()).isNotNull().isNotEmpty().isEqualTo(PASSWORD);
    }

    @Test
    public void getUserByEmail() {

        MyUser myUser = this.myUserRepository.findByEmail(EMAIL);

        assertThat(myUser).isNotNull();
        assertThat(myUser.getEmail()).isNotNull().isNotEmpty().isEqualTo(EMAIL);
        assertThat(myUser.getUsername()).isNotNull().isNotEmpty().isEqualTo(USERNAME);
        assertThat(myUser.getPassword()).isNotNull().isNotEmpty().isEqualTo(PASSWORD);
    }

    @Test
    public void getUserByEmailUsingUsername() {

        MyUser myUser = this.myUserRepository.findByEmail(USERNAME);

        assertThat(myUser).isNull();
    }

    @Test
    public void getUserByUsernameUsingEmail() {

        MyUser myUser = this.myUserRepository.findByUsername(EMAIL);

        assertThat(myUser).isNull();
    }
}
