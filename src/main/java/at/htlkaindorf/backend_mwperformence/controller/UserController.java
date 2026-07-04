package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.ChangePasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.SelfChangePasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(userService.getById(id, authentication));
    }


    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(search, pageable));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto, Authentication authentication) {
        return ResponseEntity.ok(userService.update(id, dto, authentication));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password/self")
    public ResponseEntity<Void> changeOwnPassword(@PathVariable Long id, @Valid @RequestBody SelfChangePasswordRequest request, Authentication authentication) {
        userService.changeOwnPassword(id, request, authentication);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        userService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }
}