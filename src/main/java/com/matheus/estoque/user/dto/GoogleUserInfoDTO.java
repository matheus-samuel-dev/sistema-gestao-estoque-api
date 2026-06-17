package com.matheus.estoque.user.dto;

public record GoogleUserInfoDTO(
        String email,
        String name,
        String email_verified
) {
}
