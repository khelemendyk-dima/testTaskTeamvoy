package com.my.shop.services.impl;

import com.my.shop.exceptions.PasswordMismatchException;
import com.my.shop.models.Role;
import com.my.shop.models.User;
import com.my.shop.payloads.requests.RegistrationRequest;
import com.my.shop.payloads.responses.JwtResponse;
import com.my.shop.utils.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthServiceImplTest {
    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void testCreateAuthToken() {
        JwtResponse expected = new JwtResponse("eyJhbGciOiJIUzM4NCJ9.eyJyb2xlcyI6WyJST0xFX01BTkFHRVIiXSwic3ViIjoibWFuYWdlciIsImlhdCI6MTY5NjMyODE0OSwiZXhwIjoxNjk2MzI5OTQ5fQ.nsLtSA4ke4MAoUOFGjpYUKJKKT5ipNd7AXEmmHyknBuwxjHKfbBJhTeawiWd9FAU");
        String username = "test user";

        when(userService.loadUserByUsername(username)).thenReturn(new org.springframework.security.core.userdetails.User(username, "secretPass", List.of(new SimpleGrantedAuthority(Role.ROLE_CLIENT.toString()))));
        when(jwtTokenUtil.generateToken(any())).thenReturn(expected.getToken());

        JwtResponse actual = authService.createAuthToken(username);

        assertEquals(expected, actual);
    }

    @Test
    void testRegisterNewUser() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("test user");
        request.setPassword("secretPass");
        request.setConfirmPassword("secretPass");

        User expected = new User(1L, request.getUsername(), request.getPassword(), Role.ROLE_CLIENT);

        when(userService.save(request.getUsername(), request.getPassword())).thenReturn(expected);

        User actual = authService.registerNewUser(request);

        assertEquals(expected, actual);
    }

    @Test
    void testRegisterNewUserPasswordMismatch() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("test user");
        request.setPassword("secretPass");
        request.setConfirmPassword("notMatchingString");

        assertThrows(PasswordMismatchException.class, () -> authService.registerNewUser(request));
    }

}
