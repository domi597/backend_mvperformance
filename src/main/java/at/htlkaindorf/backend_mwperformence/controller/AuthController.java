package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.AuthResponse;
import at.htlkaindorf.backend_mwperformence.dtos.ForgotPasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.LoginRequest;
import at.htlkaindorf.backend_mwperformence.dtos.PendingVerificationResponse;
import at.htlkaindorf.backend_mwperformence.dtos.RegisterRequest;
import at.htlkaindorf.backend_mwperformence.dtos.ResendVerificationRequest;
import at.htlkaindorf.backend_mwperformence.dtos.ResetPasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.dtos.VerifyEmailRequest;
import at.htlkaindorf.backend_mwperformence.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes the authentication endpoints.
 * All routes are public (no JWT required) and live under {@code /api/auth}.
 *
 * @author Nici0211
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(authService.getCurrentUser(email));
    }

    /**
     * Authenticates an existing user with their e-mail and password.
     * {@code POST /api/auth/login}
     *
     * @param request the login credentials (e-mail + password), validated via {@code @Valid}
     * @return {@code 200 OK} with an {@link AuthResponse} containing the JWT and user data
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Registers a new customer account.
     * Optionally, a vehicle can be registered together with the account if
     * {@code vehicleBrand} and {@code vehicleModel} are provided in the request body.
     * {@code POST /api/auth/register}
     *
     * @param request the registration data (personal info + optional vehicle data), validated via {@code @Valid}
     * @return {@code 201 Created} with an {@link AuthResponse} containing the JWT and user data
     */
    @PostMapping("/register")
    public ResponseEntity<PendingVerificationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.register(request));
    }

    /**
     * Bestätigt die E-Mail-Adresse eines frisch registrierten Kontos anhand des per Mail
     * verschickten 6-stelligen Codes. Bei Erfolg wird das Konto aktiviert und ein JWT ausgestellt.
     * {@code POST /api/auth/verify-email}
     *
     * @param request E-Mail-Adresse samt eingegebenem Code, validiert via {@code @Valid}
     * @return {@code 200 OK} mit einem {@link AuthResponse} inkl. JWT und Nutzerdaten
     */
    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }

    /**
     * Verschickt einen neuen Bestätigungscode für ein noch nicht verifiziertes Konto.
     * Antwortet unabhängig vom Ergebnis immer mit {@code 204 No Content}.
     * {@code POST /api/auth/resend-verification}
     *
     * @param request die E-Mail-Adresse, für die ein neuer Code verschickt werden soll
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerification(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Startet den "Passwort vergessen"-Vorgang: verschickt, falls ein Konto mit dieser
     * E-Mail existiert, einen Reset-Link. Antwortet unabhängig davon immer mit
     * {@code 204 No Content}, damit nicht erkennbar ist, ob die E-Mail registriert ist.
     * {@code POST /api/auth/forgot-password}
     *
     * @param request die E-Mail-Adresse, an die der Reset-Link geschickt werden soll
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Schließt den "Passwort vergessen"-Vorgang ab und setzt ein neues Passwort,
     * sofern der Token gültig und nicht abgelaufen ist.
     * {@code POST /api/auth/reset-password}
     *
     * @param request Reset-Token samt neuem Passwort (zweifach eingegeben)
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}