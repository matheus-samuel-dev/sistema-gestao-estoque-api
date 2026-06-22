package com.matheus.estoque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<?> handleEmailDeliveryException(
            EmailDeliveryException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "message", ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(
            RuntimeException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "message",
                                ex.getMessage() != null
                                        ? ex.getMessage()
                                        : "Não foi possível concluir a solicitação."
                        )
                );
    }
}
