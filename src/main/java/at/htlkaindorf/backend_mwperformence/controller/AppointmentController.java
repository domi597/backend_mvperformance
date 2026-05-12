package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.dtos.StatusUpdateRequest;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 11:12
 */

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * @param status optional filter by appointment status
     * @param page   page number (1-based)
     * @param size   number of entries per page
     */
    @GetMapping
    public ResponseEntity<Page<AppointmentDTO>> getAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("preferredDate").ascending());

        if (status != null) {
            return ResponseEntity.ok(appointmentService.getByStatus(status, pageable));
        }
        return ResponseEntity.ok(appointmentService.getActiveAppointments(pageable));
    }

    /**
     * @param id the ID of the appointment
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    /**
     * @param dto the appointment data to create
     */
    @PostMapping
    public ResponseEntity<AppointmentDTO> create(@RequestBody AppointmentDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointmentService.create(dto));
    }

    /**
     * @param id      the ID of the appointment to update
     * @param request the new status to set
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(
                appointmentService.updateStatus(id, request.getStatus()));
    }

    /**
     * @param id the ID of the appointment to delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}