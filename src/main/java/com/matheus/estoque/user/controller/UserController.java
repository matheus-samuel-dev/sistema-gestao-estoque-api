package com.matheus.estoque.user.controller;

import com.matheus.estoque.user.dto.RegisterDTO;
import com.matheus.estoque.user.entity.User;
import com.matheus.estoque.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.matheus.estoque.user.dto.AuthResponseDTO;
import com.matheus.estoque.user.dto.LoginDTO;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public User register(
            @RequestBody @Valid RegisterDTO dto
    ) {
        return service.register(dto);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(
            @RequestBody @Valid LoginDTO dto
    ) {
        return service.login(dto);
    }
}