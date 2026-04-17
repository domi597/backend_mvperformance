package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.mapper.AppointmentMapper;
import at.htlkaindorf.backend_mwperformence.repositories.AppointmentRepository;
import at.htlkaindorf.backend_mwperformence.repositories.ServiceRepository;
import at.htlkaindorf.backend_mwperformence.repositories.UserRepository;
import at.htlkaindorf.backend_mwperformence.repositories.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 07.04.2026
 * Time: 10:14
 */

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;

    public Page<AppointmentDTO> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toDto);
    }

    public Page<AppointmentDTO> getByStatus(AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByStatus(status, pageable)
                .map(appointmentMapper::toDto);
    }

    public AppointmentDTO getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Termin mit ID " + id + " wurde nicht gefunden."));
        return appointmentMapper.toDto(appointment);
    }

    @Transactional
    public AppointmentDTO create(AppointmentDTO dto) {
        Appointment appointment = appointmentMapper.toEntity(dto);

        if (dto.getCustomerId() != null)
            appointment.setUser(userRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User nicht gefunden")));

        if (dto.getVehicleId() != null)
            appointment.setVehicleEntity(vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fahrzeug nicht gefunden")));

        if (dto.getServiceId() != null)
            appointment.setService(serviceRepository.findById(dto.getServiceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service nicht gefunden")));

        appointment.setStatus(AppointmentStatus.NEU);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Termin nicht gefunden."));

        appointment.setStatus(status);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Transactional
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Termin existiert nicht.");
        }
        appointmentRepository.deleteById(id);
    }
}