package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.ChangePasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.SelfChangePasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user profile management.
 * All routes require a valid JWT (enforced globally by Spring Security).
 * Routes that operate on a specific {@code id} additionally enforce, at the service
 * layer, that the caller is either an ADMIN or acting on their own account.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/{id} — returns the user's profile data.
     * Only the account owner or an ADMIN may view it.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(userService.getById(id, authentication));
    }


    /**
     * GET /api/users — returns a list of all users. Restricted to ADMIN (enforced in SecurityConfig).
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }
    /**
     *  PUT /api/users/{id} — updates mutable profile fields (partial update supported).
     *  Only the account owner or an ADMIN may perform this update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto, Authentication authentication) {
        return ResponseEntity.ok(userService.update(id, dto, authentication));
    }

    /**
     *  PUT /api/users/{id}/password — admin sets a new password for the user.
     *  The previous password is never read or returned. Restricted to ADMIN (enforced in SecurityConfig).
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     *  PUT /api/users/{id}/password/self — self-service password change from the account page.
     *  Requires the current password for verification; the new password must be sent twice.
     *  A caller may only ever change their own password through this route — even an ADMIN.
     */
    @PutMapping("/{id}/password/self")
    public ResponseEntity<Void> changeOwnPassword(@PathVariable Long id, @Valid @RequestBody SelfChangePasswordRequest request, Authentication authentication) {
        userService.changeOwnPassword(id, request, authentication);
        return ResponseEntity.noContent().build();
    }

    /**
     *  DELETE /api/users/{id} — permanently deletes the account and all linked data.
     *  Only the account owner or an ADMIN may perform this.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        userService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }
}