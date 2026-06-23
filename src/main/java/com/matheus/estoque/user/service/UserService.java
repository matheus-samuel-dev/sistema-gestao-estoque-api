package com.matheus.estoque.user.service;

import com.matheus.estoque.security.JwtService;
import com.matheus.estoque.user.dto.AuthResponseDTO;
import com.matheus.estoque.user.dto.LoginDTO;
import com.matheus.estoque.user.dto.RegisterDTO;
import com.matheus.estoque.user.entity.Role;
import com.matheus.estoque.user.entity.User;
import com.matheus.estoque.user.repository.UserRepository;
import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.category.repository.CategoryRepository;
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
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;
    private final CategoryRepository categoryRepository;

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Informática", "Periféricos", "Material de Escritório", "Equipamentos",
            "Ferramentas", "Limpeza", "Elétrica", "Móveis", "Outros"
    );

    public UserService(
            UserRepository repository,
            BCryptPasswordEncoder encoder,
            JwtService jwtService,
            RestTemplate restTemplate,
            CategoryRepository categoryRepository
    ) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.restTemplate = restTemplate;
        this.categoryRepository = categoryRepository;
    }

    public User register(RegisterDTO dto) {
        if (repository.findByEmail(dto.email().trim().toLowerCase()).isPresent()) {
            throw new RuntimeException("Já existe uma conta cadastrada com este e-mail.");
        }
        Role role =
                "ADMIN".equalsIgnoreCase(dto.role())
                        ? Role.ADMIN
                        : Role.USER;

        User user = User.builder()
                .name(dto.name())
                .email(dto.email().trim().toLowerCase())
                .password(
                        encoder.encode(dto.password())
                )
                .role(role)
                .build();

        User saved = repository.save(user);
        createDefaultCategories(saved);
        return saved;
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

                            User saved = repository.save(novoUsuario);
                            createDefaultCategories(saved);
                            return saved;
                        });

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return new AuthResponseDTO(token);
    }

    private void createDefaultCategories(User user) {
        DEFAULT_CATEGORIES.forEach(name -> {
            if (!categoryRepository.existsByUserAndNameIgnoreCase(user, name)) {
                categoryRepository.save(Category.builder()
                        .name(name)
                        .description("Categoria padrão")
                        .active(true)
                        .user(user)
                        .build());
            }
        });
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
