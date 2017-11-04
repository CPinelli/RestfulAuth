package com.mycompany.security.restfulauth.services;

import com.mycompany.security.restfulauth.entities.MyUser;
import com.mycompany.security.restfulauth.repositories.MyUserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MyUserDetailsServiceTest {

    @MockBean
    private MyUserRepository myUserRepository;

    private MyUserDetailsService myUserDetailsService;

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String EMAIL = "EMAIL@EMAIL.COM";

    private static final MyUser myUser = new MyUser();
    private static final MyUser myUser2 = new MyUser();


    @Before
    public void setUp() {

        myUser.setEmail(EMAIL);
        myUser.setUsername(USERNAME);
        myUser.setPassword(PASSWORD);
        myUser.setActive(true);

        myUser2.setEmail(EMAIL + "_1");
        myUser2.setUsername(USERNAME + "_1");
        myUser2.setPassword(PASSWORD + "_1");
        myUser2.setActive(false);

        when(this.myUserRepository.findByUsername(USERNAME)).thenReturn(myUser);
        when(this.myUserRepository.findByUsername(USERNAME + "_1")).thenReturn(myUser2);

        this.myUserDetailsService = new MyUserDetailsService(this.myUserRepository);
    }

    @Test
    public void loadUserByUsername() {

        UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(USERNAME);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isNotNull().isNotEmpty().isEqualTo(USERNAME);
        assertThat(userDetails.getPassword()).isNotNull().isNotEmpty().isEqualTo(PASSWORD);
        assertThat(userDetails.getAuthorities()).isNotNull().isEmpty();
    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldThrowUsernameNotFoundException() {

        // myUser2 is not active, so it should throw an exception
        UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(USERNAME + "_1");

        assertThat(userDetails).isNull();
    }
}
