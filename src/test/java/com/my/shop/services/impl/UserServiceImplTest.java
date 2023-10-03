package com.my.shop.services.impl;

import com.my.shop.exceptions.NotFoundException;
import com.my.shop.exceptions.UserAlreadyExistsException;
import com.my.shop.models.Role;
import com.my.shop.models.User;
import com.my.shop.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testSaveUser() {
        String username = "test user";
        String password = "secretPass";
        User expected = new User(1L, username, password, Role.ROLE_CLIENT);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(expected.getPassword());
        when(userRepository.save(any())).thenReturn(expected);

        User actual = userService.save(username, password);

        assertEquals(expected, actual);
    }

    @Test
    void testSaveUserDuplicateUsername() {
        String username = "test user";
        String password = "secretPass";
        User user = new User(1L, username, password, Role.ROLE_CLIENT);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.save(username, password));
    }

    @Test
    void testFindUserById() {
        Long userId = 1L;
        User expected = new User(1L, "test user", "secretPass", Role.ROLE_CLIENT);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expected));

        User actual = userService.findById(userId);

        assertEquals(expected, actual);
    }

    @Test
    void testFindUserByIdNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void testFindUserByUsername() {
        User expected = new User(1L, "test user", "secretPass", Role.ROLE_CLIENT);

        when(userRepository.findByUsername(expected.getUsername()))
                           .thenReturn(Optional.of(expected));

        User actual = userService.findByUsername(expected.getUsername());

        assertEquals(expected, actual);
    }

    @Test
    void testFindUserByUsernameNotFound() {
        String username = "test user";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername(username));
    }
}
