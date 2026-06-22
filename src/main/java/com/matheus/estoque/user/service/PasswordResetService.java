package com.matheus.estoque.user.service;

import com.matheus.estoque.exception.EmailDeliveryException;
import com.matheus.estoque.user.entity.PasswordResetToken;
import com.matheus.estoque.user.entity.User;
import com.matheus.estoque.user.repository.PasswordResetTokenRepository;
import com.matheus.estoque.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class PasswordResetService {

    private static final String RESEND_EMAILS_URL =
            "https://api.resend.com/emails";
    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_EXPIRATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final BCryptPasswordEncoder encoder;
    private final RestTemplate restTemplate;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String resendApiKey;
    private final String mailFrom;
    private final String resetPasswordUrl;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            BCryptPasswordEncoder encoder,
            RestTemplate restTemplate,
            @Value("${app.resend.api-key}") String resendApiKey,
            @Value("${app.mail.from}") String mailFrom,
            @Value("${app.frontend.reset-password-url}") String resetPasswordUrl
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.encoder = encoder;
        this.restTemplate = restTemplate;
        this.resendApiKey = resendApiKey;
        this.mailFrom = mailFrom;
        this.resetPasswordUrl = resetPasswordUrl;
    }

    @Transactional
    public void requestReset(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        userRepository.findByEmail(email.trim().toLowerCase())
                .ifPresent(this::createAndSendResetToken);
    }

    @Transactional
    public void resetPassword(
            String token,
            String newPassword
    ) {
        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Token inválido"));

        if (Boolean.TRUE.equals(resetToken.getUsed())) {
            throw new RuntimeException("Token já utilizado");
        }

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        resetToken.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(resetToken);
    }

    private void createAndSendResetToken(User user) {
        tokenRepository.findByUserAndUsedFalse(user)
                .forEach(token -> {
                    token.setUsed(true);
                    tokenRepository.save(token);
                });

        String token = generateToken();

        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(TOKEN_EXPIRATION_MINUTES)
                        )
                        .used(false)
                        .createdAt(LocalDateTime.now())
                        .build();

        tokenRepository.save(resetToken);
        sendResetEmail(user, token);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private void sendResetEmail(
            User user,
            String token
    ) {
        validateEmailConfiguration();

        String resetLink = resetPasswordUrl + "?token=" + token;
        String safeName = HtmlUtils.htmlEscape(user.getName());
        String safeLink = HtmlUtils.htmlEscape(resetLink);

        String html =
                "<p>Olá, " + safeName + ".</p>" +
                        "<p>Recebemos uma solicitação para redefinir sua senha.</p>" +
                        "<p><a href='" + safeLink + "'>Redefinir senha</a></p>" +
                        "<p>Este link é válido por 15 minutos e só pode ser usado uma vez.</p>" +
                        "<p>Se você não solicitou esta alteração, ignore este e-mail.</p>";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(resendApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of(
                "from", mailFrom,
                "to", List.of(user.getEmail()),
                "subject", "Redefinição de senha - Sistema de Estoque",
                "html", html
        );

        try {
            restTemplate.postForEntity(
                    RESEND_EMAILS_URL,
                    new HttpEntity<>(payload, headers),
                    String.class
            );
        } catch (RestClientException exception) {
            throw new EmailDeliveryException(
                    "Não foi possível enviar o e-mail de redefinição. Tente novamente em alguns instantes."
            );
        }
    }

    private void validateEmailConfiguration() {
        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new EmailDeliveryException(
                    "O serviço de e-mail não está configurado."
            );
        }

        if (mailFrom == null || mailFrom.isBlank()) {
            throw new EmailDeliveryException(
                    "O remetente do e-mail não está configurado."
            );
        }
    }
}
