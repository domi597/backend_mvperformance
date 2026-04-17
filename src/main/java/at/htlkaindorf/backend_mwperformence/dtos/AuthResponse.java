package at.htlkaindorf.backend_mwperformence.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload returned by the authentication endpoints ({@code /api/auth/login}
 * and {@code /api/auth/register}).
 * <p>
 * Contains the signed JWT that the client must include as a {@code Bearer} token in
 * subsequent requests, together with a lightweight representation of the authenticated user.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    /** The signed JWT the client should use for authenticated requests. */
    private String token;

    /** Basic profile information about the authenticated user. */
    private UserDTO user;
}
