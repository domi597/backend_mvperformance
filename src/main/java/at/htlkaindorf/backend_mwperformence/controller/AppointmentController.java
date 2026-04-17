package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes appointment management endpoints.
 * <p>
 * Base path: {@code /api/appointments}. All requests require a valid JWT unless
 * the endpoint is explicitly marked as public in {@code SecurityConfig}.
 * </p>
 *
 * @author Dominik Ranegger
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Returns a paginated list of all appointments, optionally filtered by their status.
     * <p>
     * {@code GET /api/appointments?status=NEU&page=0&size=10}
     * </p>
     *
     * @param status optional {@link AppointmentStatus} filter; when omitted all appointments are returned
     * @param page   zero-based page index (default: {@code 1})
     * @param size   number of items per page (default: {@code 10})
     * @return {@code 200 OK} with a {@link Page} of {@link AppointmentDTO} objects
     */
    @GetMapping
    public ResponseEntity<Page<AppointmentDTO>> getAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        if (status != null) {
            return ResponseEntity.ok(appointmentService.getByStatus(status, pageable));
        }
        return ResponseEntity.ok(appointmentService.getAllAppointments(pageable));
    }
}