package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.ChangePasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user profile management.
 * All routes require a valid JWT (enforced globally by Spring Security).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/{id} — returns the user's profile data.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }


    /**
     * GET /api/users — returns a list of all users.
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }
    /**
     *  PUT /api/users/{id} — updates mutable profile fields (partial update supported).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    /**
     *  PUT /api/users/{id}/password — admin sets a new password for the user.
     *  The previous password is never read or returned.
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     *  DELETE /api/users/{id} — permanently deletes the account and all linked data.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}