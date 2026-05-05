package at.htlkaindorf.backend_mwperformence.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global exception handler that converts application exceptions into structured
 * JSON error responses.
 * Every response body contains three fields:
 * - timestamp: ISO-8601 date-time of the error
 * - status: HTTP status code as an integer
 * - error: human-readable error message
 *
 * @author Nici0211
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", ex.getStatus().value(),
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> {
                    String field = e.getField();
                    return switch (field) {
                        case "firstName"  -> "Vorname darf nicht leer sein.";
                        case "lastName"   -> "Nachname darf nicht leer sein.";
                        case "email"      -> "Bitte eine gültige E-Mail-Adresse eingeben.";
                        case "password"   -> "Passwort muss mindestens 6 Zeichen haben.";
                        default           -> e.getDefaultMessage();
                    };
                })
                .findFirst()
                .orElse("Ungültige Eingabe.");

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 400,
                "error", message
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String cause = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        String message;
        if (cause.contains("email") && cause.contains("unique")) {
            message = "Diese E-Mail-Adresse ist bereits vergeben.";
        } else if (cause.contains("too long") || cause.contains("value too long")) {
            message = "Ein eingegebener Wert ist zu lang. Bitte kürzen und erneut versuchen.";
        } else if (cause.contains("not-null") || cause.contains("null value")) {
            message = "Bitte alle Pflichtfelder ausfüllen.";
        } else {
            message = "Die Daten konnten nicht gespeichert werden. Bitte Eingaben prüfen.";
        }

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 400,
                "error", message
        ));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {

        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 403,
                "error", "Zugriff verweigert."
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 500,
                "error", "Ein unerwarteter Fehler ist aufgetreten. Bitte später erneut versuchen."
        ));
    }
}