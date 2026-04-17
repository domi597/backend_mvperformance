package at.htlkaindorf.backend_mwperformence.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service responsible for creating and validating JSON Web Tokens (JWT).
 *
 * Tokens are signed with an HMAC-SHA key derived from the configured secret and
 * carry the user's e-mail address as the subject claim. The expiration time can be
 * controlled via the {@code jwt.expiration-ms} property (default: 86 400 000 ms = 24 h).
 *
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs; // 24 h default

    /**
     * Builds the HMAC-SHA {@link SecretKey} from the configured secret string.
     *
     * @return the signing key
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT for the given e-mail address.
     * The token is valid for the duration defined by {@code jwt.expiration-ms}.
     *
     * @param email the subject to embed in the token
     * @return the compact, URL-safe JWT string
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }

    /**
     * Extracts the e-mail address (subject claim) from a JWT.
     *
     * @param token the compact JWT string
     * @return the e-mail embedded in the token
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Checks whether a JWT is structurally valid and has not yet expired.
     *
     * @param token the compact JWT string to verify
     * @return {@code true} if the token is valid and not expired; {@code false} otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            return parseClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses and verifies the JWT signature, returning the contained {@link Claims}.
     * Throws a {@link io.jsonwebtoken.JwtException} if the token is tampered or malformed.
     *
     * @param token the compact JWT string
     * @return the verified claims payload
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
