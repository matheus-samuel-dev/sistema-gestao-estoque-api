package com.matheus.estoque.user.service;

import com.matheus.estoque.security.JwtService;
import com.matheus.estoque.user.dto.AuthResponseDTO;
import com.matheus.estoque.user.dto.LoginDTO;
import com.matheus.estoque.user.dto.RegisterDTO;
import com.matheus.estoque.user.entity.Role;
import com.matheus.estoque.user.entity.User;
import com.matheus.estoque.user.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    public UserService(
            UserRepository repository,
            BCryptPasswordEncoder encoder,
            JwtService jwtService,
            RestTemplate restTemplate
    ) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.restTemplate = restTemplate;
    }

    public User register(RegisterDTO dto) {
        Role role =
                "ADMIN".equalsIgnoreCase(dto.role())
                        ? Role.ADMIN
                        : Role.USER;

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(
                        encoder.encode(dto.password())
                )
                .role(role)
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

    public AuthResponseDTO googleLogin(String googleToken) {
        Map<String, Object> googleUser =
                loadGoogleUser(googleToken);

        if (googleUser == null) {
            throw new RuntimeException("Token Google inválido");
        }

        String email = String.valueOf(
                googleUser.getOrDefault("email", "")
        );

        String name = String.valueOf(
                googleUser.getOrDefault("name", email)
        );

        Object verifiedValue =
                googleUser.get("email_verified");

        boolean emailVerified =
                verifiedValue == null ||
                        Boolean.parseBoolean(
                                String.valueOf(verifiedValue)
                        );

        if (email.isBlank() || !emailVerified) {
            throw new RuntimeException(
                    "Conta Google inválida ou e-mail não verificado"
            );
        }

        User user =
                repository.findByEmail(email)
                        .orElseGet(() -> {
                            User novoUsuario =
                                    User.builder()
                                            .name(name)
                                            .email(email)
                                            .password(
                                                    encoder.encode(
                                                            UUID.randomUUID()
                                                                    .toString()
                                                    )
                                            )
                                            .role(Role.USER)
                                            .build();

                            return repository.save(novoUsuario);
                        });

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return new AuthResponseDTO(token);
    }

    private Map<String, Object> loadGoogleUser(String googleToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(googleToken);

            ResponseEntity<Map> response =
                    restTemplate.exchange(
                            "https://www.googleapis.com/oauth2/v3/userinfo",
                            HttpMethod.GET,
                            new HttpEntity<>(headers),
                            Map.class
                    );

            return response.getBody();
        } catch (Exception ignored) {
            String encodedToken =
                    URLEncoder.encode(
                            googleToken,
                            StandardCharsets.UTF_8
                    );

            return restTemplate.getForObject(
                    "https://oauth2.googleapis.com/tokeninfo?id_token="
                            + encodedToken,
                    Map.class
            );
        }
    }
}
