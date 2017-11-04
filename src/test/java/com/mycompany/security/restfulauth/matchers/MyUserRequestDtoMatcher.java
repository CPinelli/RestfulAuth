package com.mycompany.security.restfulauth.matchers;

import com.mycompany.security.restfulauth.dto.MyUserRequestDto;

import org.mockito.ArgumentMatcher;

public class MyUserRequestDtoMatcher extends ArgumentMatcher<MyUserRequestDto> {

    private final MyUserRequestDto expected;

    public MyUserRequestDtoMatcher(MyUserRequestDto expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object o) {

        if (o == null || !(o instanceof MyUserRequestDto)) {
            return false;
        }

        MyUserRequestDto toCompare = (MyUserRequestDto) o;
        return toCompare.getEmail().equals(expected.getEmail()) && toCompare.getUsername().equals(
                expected.getUsername()) && toCompare.getPassword().equals(expected.getPassword());
    }
}
