package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.DateRequestDTO;
import at.htlkaindorf.backend_mwperformence.dtos.TimeslotDTO;
import at.htlkaindorf.backend_mwperformence.services.TimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 22.05.2026
 * Time: 11:06
 */

@RestController
@RequestMapping("/api/timeslots")
@RequiredArgsConstructor
public class TimeslotController {

    private final TimeslotService timeslotService;

    @GetMapping
    public ResponseEntity<List<TimeslotDTO>> getAll() {
        return ResponseEntity.ok(timeslotService.getAll());
    }

    @PostMapping
    public ResponseEntity<List<TimeslotDTO>> getAvailable(@RequestParam LocalDate date) {
        return ResponseEntity.ok(timeslotService.getAvailable(date));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timeslotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
