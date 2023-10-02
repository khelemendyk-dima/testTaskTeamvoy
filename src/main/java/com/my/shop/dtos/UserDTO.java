package com.my.shop.dtos;

import com.my.shop.models.Role;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDTO {
    private Long id;

    @Size(min = 1, max = 45, message = "Username must be between 1 and 45 characters")
    @NotBlank(message = "Username must not be empty")
    private String username;

    private Role role;
}
