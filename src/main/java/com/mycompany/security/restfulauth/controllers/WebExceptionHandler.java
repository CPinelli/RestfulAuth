package com.mycompany.security.restfulauth.controllers;

import com.mycompany.security.restfulauth.exceptions.AlreadyExistsException;
import com.mycompany.security.restfulauth.exceptions.UnauthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class WebExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AlreadyExistsException.class)
    public Map<String, String> handleAlreadyExists(Exception e) {

        Map<String, String> response = new HashMap<>();
        response.put("code", "409");
        response.put("message", e.getLocalizedMessage());

        return response;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Map<String, String> handleUnauthorized(Exception e) {

        Map<String, String> response = new HashMap<>();
        response.put("code", "401");
        response.put("message", e.getMessage());

        return response;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(IOException.class)
    public Map<String, String> handleIO(Exception e) {

        Map<String, String> response = new HashMap<>();
        response.put("code", "401");
        response.put("message", e.getMessage());

        return response;
    }
}
