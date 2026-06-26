package com.matheus.estoque.exception;

import com.matheus.estoque.attachment.exception.AttachmentStorageException;
import jakarta.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );
        return error(HttpStatus.BAD_REQUEST, "Revise os campos destacados.", errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleConflict(DataIntegrityViolationException ex) {
        return error(HttpStatus.CONFLICT, "Já existe um registro com essas informações.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleLargeFile(MaxUploadSizeExceededException ex) {
        return error(HttpStatus.BAD_REQUEST, "O arquivo selecionado ultrapassa o limite de 5MB.");
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<?> handleEmailDeliveryException(EmailDeliveryException ex) {
        return error(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    @ExceptionHandler(AttachmentStorageException.class)
    public ResponseEntity<?> handleAttachmentStorageException(AttachmentStorageException ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler({PersistenceException.class, HibernateException.class, DataAccessException.class})
    public ResponseEntity<?> handlePersistenceException(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, "Ocorreu um erro inesperado. Tente novamente em alguns instantes.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage() != null ? ex.getMessage() : "Não foi possível concluir a solicitação."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Tente novamente em alguns instantes.");
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return error(status, message, null);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message, Map<String, String> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        body.put("status", status.value());
        if (errors != null && !errors.isEmpty()) {
            body.put("errors", errors);
        }
        return ResponseEntity.status(status).body(body);
    }
}
