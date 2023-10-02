package com.my.shop.services;

import com.my.shop.models.User;
import com.my.shop.payloads.requests.RegistrationRequest;
import com.my.shop.payloads.responses.JwtResponse;

public interface AuthService {
    JwtResponse createAuthToken(String username);
    User registerNewUser(RegistrationRequest request);
}
