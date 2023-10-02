package com.my.shop.controllers;

import com.my.shop.dtos.UserDTO;
import com.my.shop.payloads.requests.LoginRequest;
import com.my.shop.payloads.requests.RegistrationRequest;
import com.my.shop.payloads.responses.JwtResponse;
import com.my.shop.services.AuthService;
import com.my.shop.utils.ConvertorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final ConvertorUtil convertor;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        JwtResponse response= authService.createAuthToken(loginRequest.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registration")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid RegistrationRequest request) {
        UserDTO newUser = convertor.convertToUserDTO(authService.registerNewUser(request));

        return ResponseEntity.ok(newUser);
    }
}
