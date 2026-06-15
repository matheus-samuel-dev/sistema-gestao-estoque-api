package com.matheus.estoque.user.dto;

import com.matheus.estoque.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(

        @NotBlank
        String name,

        @Email
        String email,

        @NotBlank
        String password,

        Role role
) {
}