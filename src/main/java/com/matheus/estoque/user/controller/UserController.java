package com.matheus.estoque.user.controller;

import com.matheus.estoque.user.dto.AuthResponseDTO;
import com.matheus.estoque.user.dto.ForgotPasswordDTO;
import com.matheus.estoque.user.dto.GoogleLoginDTO;
import com.matheus.estoque.user.dto.LoginDTO;
import com.matheus.estoque.user.dto.MessageResponseDTO;
import com.matheus.estoque.user.dto.RegisterDTO;
import com.matheus.estoque.user.dto.ResetPasswordDTO;
import com.matheus.estoque.user.entity.User;
import com.matheus.estoque.user.service.PasswordResetService;
import com.matheus.estoque.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService service;
    private final PasswordResetService passwordResetService;

    public UserController(
            UserService service,
            PasswordResetService passwordResetService
    ) {
        this.service = service;
        this.passwordResetService = passwordResetService;
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

    @PostMapping("/google")
    public AuthResponseDTO googleLogin(
            @RequestBody @Valid GoogleLoginDTO dto
    ) {
        return service.googleLogin(
                dto.token()
        );
    }

    @PostMapping("/forgot-password")
    public MessageResponseDTO forgotPassword(
            @RequestBody @Valid ForgotPasswordDTO dto
    ) {
        passwordResetService.requestReset(dto.email());

        return new MessageResponseDTO(
                "Se este e-mail estiver cadastrado, enviaremos as instruções de redefinição."
        );
    }

    @PostMapping("/reset-password")
    public MessageResponseDTO resetPassword(
            @RequestBody @Valid ResetPasswordDTO dto
    ) {
        passwordResetService.resetPassword(
                dto.token(),
                dto.newPassword()
        );

        return new MessageResponseDTO(
                "Senha redefinida com sucesso."
        );
    }
}
