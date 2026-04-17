package at.htlkaindorf.backend_mwperformence.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global exception handler that converts application exceptions into structured
 * JSON error responses.
 * <p>
 * Every response body contains three fields:
 * <ul>
 *   <li>{@code timestamp} – ISO-8601 date-time of the error</li>
 *   <li>{@code status} – HTTP status code as an integer</li>
 *   <li>{@code error} – human-readable error message</li>
 * </ul>
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link ApiException}s thrown by the service layer.
     * The HTTP status and message are taken directly from the exception.
     *
     * @param ex the caught {@link ApiException}
     * @return a structured error response with the exception's HTTP status
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", ex.getStatus().value(),
                "error", ex.getMessage()
        ));
    }

    /**
     * Handles Bean Validation failures (e.g. a missing required field or an invalid e-mail format).
     * Returns the first field-level constraint violation as the error message.
     *
     * @param ex the validation exception containing all constraint violations
     * @return {@code 400 Bad Request} with a description of the first field error
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Ungültige Eingabe");

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 400,
                "error", message
        ));
    }

    /**
     * Handles {@link org.springframework.security.access.AccessDeniedException} thrown
     * when an authenticated user tries to access a resource they are not authorised for
     * (e.g. a {@code CUSTOMER} accessing an {@code ADMIN}-only endpoint).
     *
     * @param ex the access-denied exception
     * @return {@code 403 Forbidden} with a generic access-denied message
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {

        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 403,
                "error", "Zugriff verweigert"
        ));
    }
}
