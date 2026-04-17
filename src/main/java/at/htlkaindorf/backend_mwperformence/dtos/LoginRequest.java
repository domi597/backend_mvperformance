package at.htlkaindorf.backend_mwperformence.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request payload for the {@code POST /api/auth/login} endpoint.
 * <p>
 * Both fields are mandatory and are validated via Bean Validation before the
 * request reaches the service layer.
 * </p>
 */
@Data
public class LoginRequest {

    /** The user's e-mail address. Must be a well-formed e-mail and not blank. */
    @NotBlank
    @Email
    private String email;

    /** The user's plain-text password. Must not be blank. */
    @NotBlank
    private String password;
}
