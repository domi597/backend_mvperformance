package at.htlkaindorf.backend_mwperformence.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request payload for {@code POST /api/auth/verify-email}.
 * Confirms a freshly registered account by matching the 6-digit code
 * that was sent to the user's e-mail address.
 */
@Data
public class VerifyEmailRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "Der Code muss aus 6 Ziffern bestehen.")
    private String code;
}
