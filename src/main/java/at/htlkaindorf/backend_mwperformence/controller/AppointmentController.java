package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.dtos.StatusUpdateRequest;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @param page   page number (0-based)
     * @param size   number of entries per page
     */
    @GetMapping
    public ResponseEntity<Page<AppointmentDTO>> getAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) boolean todayOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("preferredDate").ascending());

        if (todayOnly) {
            return ResponseEntity.ok(appointmentService.getTodayAppointments(pageable));
        }
        if (status != null) {
            return ResponseEntity.ok(appointmentService.getByStatus(status, pageable));
        }
        return ResponseEntity.ok(appointmentService.getActiveAppointments(pageable));
    }

    /**
     * Liefert alle Termine für die Admin-Kalenderansicht (Monat/Woche/Tag).
     * Enthält auch abgeschlossene und vergangene Termine, nur abgelehnte
     * Termine werden ausgeblendet. Unpaginiert.
     */
    @GetMapping("/calendar")
    public ResponseEntity<List<AppointmentDTO>> getCalendarAppointments() {
        return ResponseEntity.ok(appointmentService.getCalendarAppointments());
    }

    /**
     * Liefert alle Termine des eingeloggten Kunden (vergangene und zukünftige).
     * Abgelehnte Termine werden ausgeblendet. Unpaginiert.
     */
    @GetMapping("/my")
    public ResponseEntity<List<AppointmentDTO>> getMyAppointments(Authentication authentication) {
        return ResponseEntity.ok(appointmentService.getMyAppointments(authentication));
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
     * Storniert einen eigenen Termin (Kunden-Selbstbedienung). Nur der Besitzer des
     * Termins oder ein ADMIN darf ihn stornieren.
     *
     * @param id the ID of the appointment to cancel
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancel(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, authentication));
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