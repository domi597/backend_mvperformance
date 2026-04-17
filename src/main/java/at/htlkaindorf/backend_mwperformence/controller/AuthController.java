package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.AuthResponse;
import at.htlkaindorf.backend_mwperformence.dtos.LoginRequest;
import at.htlkaindorf.backend_mwperformence.dtos.RegisterRequest;
import at.htlkaindorf.backend_mwperformence.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes the authentication endpoints.
 * <p>
 * All routes are public (no JWT required) and live under {@code /api/auth}.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates an existing user with their e-mail and password.
     * <p>
     * {@code POST /api/auth/login}
     * </p>
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
     * <p>
     * {@code POST /api/auth/register}
     * </p>
     *
     * @param request the registration data (personal info + optional vehicle data), validated via {@code @Valid}
     * @return {@code 201 Created} with an {@link AuthResponse} containing the JWT and user data
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
}
