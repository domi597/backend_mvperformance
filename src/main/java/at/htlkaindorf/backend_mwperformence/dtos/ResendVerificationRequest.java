package at.htlkaindorf.backend_mwperformence.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request payload for {@code POST /api/auth/resend-verification}.
 */
@Data
public class ResendVerificationRequest {

    @NotBlank
    @Email
    private String email;
}
