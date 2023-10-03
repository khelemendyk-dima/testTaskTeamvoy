package com.my.shop.services.impl;

import com.my.shop.exceptions.PasswordMismatchException;
import com.my.shop.models.User;
import com.my.shop.payloads.requests.RegistrationRequest;
import com.my.shop.payloads.responses.JwtResponse;
import com.my.shop.services.AuthService;
import com.my.shop.services.UserService;
import com.my.shop.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public JwtResponse createAuthToken(String username) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);

        return new JwtResponse(token);
    }

    @Override
    public User registerNewUser(RegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords doesn't match");
        }

        return userService.save(request.getUsername(), request.getPassword());
    }
}
