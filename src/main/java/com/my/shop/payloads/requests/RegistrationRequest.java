package com.my.shop.payloads.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegistrationRequest {
    @Size(min = 1, max = 45, message = "Username must be between 1 and 45 characters")
    private String username;

    @NotBlank(message = "Password must not be empty")
    private String password;

    @NotBlank(message = "Confirm password must not be empty")
    private String confirmPassword;
}
