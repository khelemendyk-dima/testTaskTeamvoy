package com.my.shop.services.impl;

import com.my.shop.exceptions.UserAlreadyExistsException;
import com.my.shop.models.Role;
import com.my.shop.models.User;
import com.my.shop.payloads.requests.RegistrationRequest;
import com.my.shop.repositories.UserRepository;
import com.my.shop.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User save(RegistrationRequest request) {
        Optional<User> userFromDB = userRepository.findByUsername(request.getUsername());

        if (userFromDB.isPresent()) {
            throw new UserAlreadyExistsException(
                    String.format("User with username '%s' is already in use", request.getUsername()));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_CLIENT);

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User with username '%s' not found", username));
        }

        return user.get();
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );
    }
}
