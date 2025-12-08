package com.jdk.encher.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🧠 Gestion globale des exceptions avec messages clairs et format JSON
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 404 - Entité non trouvée
    @ExceptionHandler({EntityNotFoundException.class, org.springframework.security.core.userdetails.UsernameNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFound(Exception ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    // 🟠 400 - Erreurs de validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return buildErrorResponse("Erreur de validation", HttpStatus.BAD_REQUEST, request, errors);
    }

    // 🟡 400 - Violations de contrainte
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return buildErrorResponse("Violation de contrainte", HttpStatus.BAD_REQUEST, request, errors);
    }

    // 🔵 401 - Identifiants incorrects
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        return buildErrorResponse("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED, request);
    }

    // 🟣 403 - Accès refusé
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse("Accès refusé : " + ex.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    // ⚫ 500 - Autres erreurs
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // 🧩 Méthodes utilitaires
    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        return buildErrorResponse(message, status, request, null);
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status, WebRequest request, Map<String, String> details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        if (details != null) body.put("details", details);
        return new ResponseEntity<>(body, new HttpHeaders(), status);
    }
}
