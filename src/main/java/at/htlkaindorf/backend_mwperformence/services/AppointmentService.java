package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.entites.ServiceEntity;
import at.htlkaindorf.backend_mwperformence.entites.Vehicle;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;

    public Page<AppointmentDTO> getActiveAppointments(Pageable pageable) {
        return appointmentRepository.findByStatusNotIn(
                        List.of(AppointmentStatus.ABGESCHLOSSEN, AppointmentStatus.ABGELEHNT), pageable)
                .map(appointmentMapper::toDto);
    }

    public Page<AppointmentDTO> getTodayAppointments(Pageable pageable) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByPreferredDateBetween(
                        today.atStartOfDay(), today.atTime(23, 59, 59), pageable)
                .map(appointmentMapper::toDto);
    }

    public Page<AppointmentDTO> getByStatus(AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByStatus(status, pageable)
                .map(appointmentMapper::toDto);
    }

    /**
     * Liefert alle Termine für die Kalenderansicht (Monat/Woche/Tag im Admin-Bereich).
     * Zeigt bewusst auch abgeschlossene und vergangene Termine, nur abgelehnte
     * Termine werden ausgeblendet. Unpaginiert, da der Kalender den kompletten
     * Datensatz für die aktuelle Ansicht braucht.
     */
    public List<AppointmentDTO> getCalendarAppointments() {
        return appointmentMapper.toDto(
                appointmentRepository.findByStatusNotInOrderByPreferredDateAsc(
                        List.of(AppointmentStatus.ABGELEHNT)));
    }

    public AppointmentDTO getById(Long id) {
        return appointmentMapper.toDto(
                appointmentRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Termin mit ID " + id + " nicht gefunden.")));
    }

    @Transactional
    public AppointmentDTO create(AppointmentDTO dto) {

        Appointment appointment = appointmentMapper.toEntity(dto);

        if (appointment.getPrice() == null) {
            appointment.setPrice(0.0);
        }

        if (dto.getCustomerId() != null) {
            appointment.setUser(
                    userRepository.findById(dto.getCustomerId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User nicht gefunden")));
        }

        if (dto.getLicensePlate() != null) {
            var v = vehicleRepository.findByLicensePlate(dto.getLicensePlate())
                    .orElseGet(() -> vehicleRepository.save(Vehicle.builder()
                            .brand(dto.getBrand())
                            .model(dto.getModel())
                            .buildYear(dto.getYear())
                            .licensePlate(dto.getLicensePlate())
                            .user(appointment.getUser())
                            .build()));
            appointment.setVehicleEntity(v);
            appointment.setVehicle(v.getBrand() + " " + v.getModel() + " " + v.getBuildYear());
        }

        Optional<ServiceEntity> serviceOpt = Optional.empty();

        if (dto.getServiceId() != null) {
            serviceOpt = serviceRepository.findById(dto.getServiceId());
        }
        if (serviceOpt.isEmpty() && dto.getServiceType() != null && !dto.getServiceType().isBlank()) {
            serviceOpt = serviceRepository.findByTitleIgnoreCase(dto.getServiceType());
        }

        if (serviceOpt.isPresent()) {
            ServiceEntity s = serviceOpt.get();
            appointment.setServiceEntity(s);
            appointment.setServiceType(s.getTitle());
        }

        appointment.setStatus(AppointmentStatus.NEU);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Termin nicht gefunden."));
        appointment.setStatus(status);
        return appointmentMapper.toDto(appointmentRepository.save(appointment));
    }

    @Transactional
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Termin existiert nicht.");
        appointmentRepository.deleteById(id);
    }
}