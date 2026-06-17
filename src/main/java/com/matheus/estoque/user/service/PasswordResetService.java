package com.matheus.estoque.user.service;

import com.matheus.estoque.user.entity.PasswordResetToken;
import com.matheus.estoque.user.entity.User;
import com.matheus.estoque.user.repository.PasswordResetTokenRepository;
import com.matheus.estoque.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_EXPIRATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final BCryptPasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String mailFrom;
    private final String resetPasswordUrl;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            BCryptPasswordEncoder encoder,
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String mailFrom,
            @Value("${app.frontend.reset-password-url}") String resetPasswordUrl
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.encoder = encoder;
        this.mailSender = mailSender;
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
        user.setPassword(
                encoder.encode(newPassword)
        );

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
        String resetLink =
                resetPasswordUrl + "?token=" + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        if (mailFrom != null && !mailFrom.isBlank()) {
            message.setFrom(mailFrom);
        }

        message.setTo(user.getEmail());
        message.setSubject("Redefinição de senha - Sistema de Estoque");
        message.setText(
                "Olá, " + user.getName() + ".\n\n" +
                        "Recebemos uma solicitação para redefinir sua senha.\n" +
                        "Clique no link abaixo para criar uma nova senha:\n\n" +
                        resetLink + "\n\n" +
                        "Este link é válido por 15 minutos e só pode ser usado uma vez.\n\n" +
                        "Se você não solicitou essa alteração, ignore este e-mail."
        );

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar e-mail de redefinição: " + e.getMessage());
        }
    }
}
