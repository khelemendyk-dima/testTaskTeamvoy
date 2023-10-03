package com.my.shop.services;

import com.my.shop.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(String username, String password);
    User findByUsername(String username);
    User findById(Long userId);
}
