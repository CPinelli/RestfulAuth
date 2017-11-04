package com.mycompany.security.restfulauth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.security.restfulauth.dto.MyUserRequestDto;
import com.mycompany.security.restfulauth.entities.MyUser;
import com.mycompany.security.restfulauth.exceptions.AlreadyExistsException;
import com.mycompany.security.restfulauth.repositories.MyUserRepository;
import com.mycompany.security.restfulauth.services.MyUserDetailsService;
import com.mycompany.security.restfulauth.services.MyUserService;
import com.mycompany.security.restfulauth.utils.HashUtils;
import com.mycompany.security.restfulauth.utils.SecurityUtils;
import com.mycompany.security.restfulauth.utils.UriUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@EnableWebMvc
@SpringBootTest
public class AuthTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @MockBean
    private MyUserRepository myUserRepository;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    private MyUserService myUserService;

    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";

    @Before
    public void setup() throws AlreadyExistsException {

        this.myUserService = new MyUserService(this.myUserRepository);

        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();

        MyUser myUser = new MyUser();

        myUser.setUsername(USERNAME);
        myUser.setEmail(EMAIL);
        myUser.setPassword(PASSWORD);

        MyUserRequestDto myUserRequestDto = new MyUserRequestDto();

        myUserRequestDto.setUsername(USERNAME);
        myUserRequestDto.setEmail(EMAIL);
        myUserRequestDto.setPassword(PASSWORD);

        when(this.myUserRepository.findByUsername(eq(USERNAME))).thenReturn(null);
        when(this.myUserRepository.findByEmail(eq(EMAIL))).thenReturn(null);
        when(this.myUserRepository.findByUsername(eq(EMAIL))).thenReturn(myUser);
    }

    @Test
    public void shouldSignup() throws Exception {

        MyUserRequestDto myUserRequestDto = new MyUserRequestDto();

        myUserRequestDto.setUsername(USERNAME);
        myUserRequestDto.setEmail(EMAIL);
        myUserRequestDto.setPassword(PASSWORD);

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(myUserRequestDto);

        this.mvc.perform(
                post(UriUtils.SIGN_UP_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldThrowAlreadyExistsException() throws Exception {

        MyUserRequestDto myUserRequestDto = new MyUserRequestDto();

        myUserRequestDto.setUsername(EMAIL);
        myUserRequestDto.setEmail(EMAIL);
        myUserRequestDto.setPassword(PASSWORD);

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(myUserRequestDto);

        this.mvc.perform(
                post(UriUtils.SIGN_UP_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldLogin() throws Exception {

        this.mvc = MockMvcBuilders.webAppContextSetup(context).addFilter(springSecurityFilterChain).build();

        MyUserRequestDto myUserRequestDto = new MyUserRequestDto();

        myUserRequestDto.setUsername(USERNAME);
        myUserRequestDto.setPassword(PASSWORD);

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(myUserRequestDto);

        String hash = HashUtils.hash(PASSWORD);

        MyUser myUser = new MyUser();

        myUser.setUsername(USERNAME);
        myUser.setPassword(hash);

        when(this.myUserDetailsService.loadUserByUsername(USERNAME)).thenReturn(
                new User(USERNAME, hash, Collections.emptyList()));

        MvcResult mvcResult = this.mvc.perform(
                post(UriUtils.LOGIN_URI)
                        .content(json))
                .andExpect(status().isOk()).andReturn();

        String headerValue = mvcResult.getResponse().getHeader(SecurityUtils.HEADER);

        String jwtRegex = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";
        assertThat(headerValue).isNotNull().isNotEmpty().containsPattern(jwtRegex);
    }
}
