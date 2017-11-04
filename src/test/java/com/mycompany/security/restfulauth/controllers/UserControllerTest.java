package com.mycompany.security.restfulauth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.security.restfulauth.dto.MyUserRequestDto;
import com.mycompany.security.restfulauth.dto.MyUserResponseDto;
import com.mycompany.security.restfulauth.entities.MyUser;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@EnableWebMvc
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private MyUserService myUserService;

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).addFilter(springSecurityFilterChain).build();

        List<MyUser> myUsers = new ArrayList<>();

        MyUser myUser = new MyUser();

        myUser.setActive(true);
        myUser.setUsername(USERNAME);
        myUser.setEmail(EMAIL);
        myUser.setPassword(HashUtils.hash(PASSWORD));

        MyUser myUser1 = new MyUser();

        myUser1.setActive(true);
        myUser1.setUsername(USERNAME + "_1");
        myUser1.setEmail(EMAIL + "_1");
        myUser1.setPassword(HashUtils.hash(PASSWORD + "_1"));

        myUsers.add(myUser);
        myUsers.add(myUser1);

        when(this.myUserService.getAllActiveUsers(USERNAME)).thenReturn(myUsers);
    }

    public String login() throws Exception {
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

        return mvcResult.getResponse().getHeader(SecurityUtils.HEADER);
    }

    @Test
    public void getAllUsers() throws Exception {

        String token = this.login();

        MvcResult mvcResult = this.mvc.perform(
                get("/users")
                        .header(SecurityUtils.HEADER, token))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        List<MyUserResponseDto> myUserResponseDtos = new ObjectMapper().readValue(response.getContentAsString(),
                                                                                  List.class);

        assertThat(myUserResponseDtos).isNotNull();
        assertThat(myUserResponseDtos).isNotEmpty();
        assertThat(myUserResponseDtos).hasSize(2);
    }

    @Test
    public void shouldThrowUnauthorized() throws Exception {

        this.mvc.perform(
                get("/users"))
                .andExpect(status().isUnauthorized());
    }
}
