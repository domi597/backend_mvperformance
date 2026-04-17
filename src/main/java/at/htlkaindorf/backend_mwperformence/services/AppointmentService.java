package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.mapper.AppointmentMapper;
import at.htlkaindorf.backend_mwperformence.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service layer for appointment-related business logic.
 * <p>
 * Provides paginated access to appointments, with optional filtering by
 * {@link AppointmentStatus}. Results are mapped to {@link AppointmentDTO}s
 * before being returned to the controller.
 * </p>
 *
 * @author Dominik Ranegger
 */
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    /**
     * Returns all appointments in a paginated format.
     *
     * @param pageable pagination and sorting parameters
     * @return a {@link Page} of {@link AppointmentDTO}s
     */
    public Page<AppointmentDTO> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toDto);
    }

    /**
     * Returns all appointments with a specific {@link AppointmentStatus} in a paginated format.
     *
     * @param status   the status to filter by
     * @param pageable pagination and sorting parameters
     * @return a {@link Page} of {@link AppointmentDTO}s matching the given status
     */
    public Page<AppointmentDTO> getByStatus(AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByStatus(status, pageable)
                .map(appointmentMapper::toDto);
    }
}