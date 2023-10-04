package com.my.shop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.shop.dtos.UserDTO;
import com.my.shop.exceptions.NotFoundException;
import com.my.shop.exceptions.PasswordMismatchException;
import com.my.shop.exceptions.UserAlreadyExistsException;
import com.my.shop.models.Role;
import com.my.shop.models.User;
import com.my.shop.payloads.responses.JwtResponse;
import com.my.shop.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authManager;
    @MockBean
    private AuthServiceImpl authService;

    @Test
    void testAuthenticateUser() throws Exception {
        String username = "test user";
        String password = "secretWord";
        JwtResponse response = new JwtResponse("someSecretString");
        String expectedJson = objectMapper.writeValueAsString(response);
        String loginRequestJson = "{\"username\": \""+username+"\", " +
                                   "\"password\": \""+password+"\"}";

        when(authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenReturn(new UsernamePasswordAuthenticationToken(username, password));
        when(authService.createAuthToken(username)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    void testAuthenticateUserNotFound() throws Exception {
        String username = "test user";
        String password = "secretWord";
        String loginRequestJson = "{\"username\": \""+username+"\", " +
                                   "\"password\": \""+password+"\"}";

        when(authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequestJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testAuthenticateUserBadCredential() throws Exception {
        String username = "test user";
        String password = "secretWord";
        String loginRequestJson = "{\"username\": \""+username+"\", " +
                "\"password\": \""+password+"\"}";

        when(authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenThrow(BadCredentialsException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequestJson))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testRegisterUser() throws Exception {
        String registrationRequestJson = "{\"username\": \"test user\", " +
                                          "\"password\": \"secretWord\"," +
                                          "\"confirmPassword\" : \"secretWord\"}";
        User user = new User(1L, "test user", "secretWord", Role.ROLE_CLIENT);
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getRole());
        String expectedJson = objectMapper.writeValueAsString(userDTO);

        when(authService.registerNewUser(any())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationRequestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    void testRegisterUserPasswordsMismatch() throws Exception {
        String registrationRequestJson = "{\"username\": \"test user\", " +
                                          "\"password\": \"secretWord\"," +
                                          "\"confirmPassword\" : \"anotherString\"}";

        when(authService.registerNewUser(any())).thenThrow(PasswordMismatchException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationRequestJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void testRegisterUserAlreadyExists() throws Exception {
        String registrationRequestJson = "{\"username\": \"test user\", " +
                                          "\"password\": \"secretWord\"," +
                                          "\"confirmPassword\" : \"secretWord\"}";

        when(authService.registerNewUser(any())).thenThrow(UserAlreadyExistsException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationRequestJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}
