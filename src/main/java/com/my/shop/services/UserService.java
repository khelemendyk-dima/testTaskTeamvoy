package com.my.shop.services;

import com.my.shop.models.User;
import com.my.shop.payloads.requests.RegistrationRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(RegistrationRequest request);
    User findByUsername(String username);
}
