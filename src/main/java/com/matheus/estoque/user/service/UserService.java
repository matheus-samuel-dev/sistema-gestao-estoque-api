package com.matheus.estoque.user.service;

import com.matheus.estoque.user.dto.RegisterDTO;
import com.matheus.estoque.user.entity.User;
import com.matheus.estoque.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.matheus.estoque.security.JwtService;
import com.matheus.estoque.user.dto.AuthResponseDTO;
import com.matheus.estoque.user.dto.LoginDTO;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository repository,
            BCryptPasswordEncoder encoder,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterDTO dto) {

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(
                        encoder.encode(dto.password())
                )
                .role(dto.role())
                .build();

        return repository.save(user);
    }

    public AuthResponseDTO login(LoginDTO dto) {

        User user = repository.findByEmail(dto.email())
                .orElseThrow(
                        () -> new RuntimeException("Usuário não encontrado")
                );

        boolean passwordMatches =
                encoder.matches(
                        dto.password(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException("Senha inválida");
        }

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return new AuthResponseDTO(token);
    }
}