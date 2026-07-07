package at.htlkaindorf.backend_mwperformence.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned by {@code POST /api/auth/register}.
 * No JWT is issued yet — the account only becomes usable once the
 * 6-digit code sent to {@code email} is confirmed via {@code /api/auth/verify-email}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingVerificationResponse {

    private String email;

    private String message;
}
