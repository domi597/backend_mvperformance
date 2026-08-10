package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.BlockedPeriodDTO;
import at.htlkaindorf.backend_mwperformence.services.BlockedPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * <p>
 * GET ist öffentlich (wird auf der öffentlichen Terminseite /termin genutzt,
 * um gesperrte Tage/Uhrzeiten anzuzeigen). POST/DELETE sind admin-only, siehe
 * SecurityConfig.
 */
@RestController
@RequestMapping("/api/blocked-periods")
@RequiredArgsConstructor
public class BlockedPeriodController {

    private final BlockedPeriodService blockedPeriodService;

    @GetMapping
    public ResponseEntity<List<BlockedPeriodDTO>> getAll() {
        return ResponseEntity.ok(blockedPeriodService.getAll());
    }

    @PostMapping
    public ResponseEntity<BlockedPeriodDTO> create(@RequestBody BlockedPeriodDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blockedPeriodService.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blockedPeriodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
