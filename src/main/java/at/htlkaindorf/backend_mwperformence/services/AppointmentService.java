package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.entites.Offer;
import at.htlkaindorf.backend_mwperformence.entites.ServiceEntity;
import at.htlkaindorf.backend_mwperformence.entites.Vehicle;
import at.htlkaindorf.backend_mwperformence.mapper.AppointmentMapper;
import at.htlkaindorf.backend_mwperformence.repositories.AppointmentRepository;
import at.htlkaindorf.backend_mwperformence.repositories.OfferRepository;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int DEFAULT_DURATION_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final OfferRepository offerRepository;
    private final MailService mailService;

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
            String normalizedPlate = Vehicle.normalize(dto.getLicensePlate());
            var v = vehicleRepository.findByLicensePlate(normalizedPlate)
                    .orElseGet(() -> vehicleRepository.save(Vehicle.builder()
                            .brand(dto.getBrand())
                            .model(dto.getModel())
                            .buildYear(dto.getYear())
                            .licensePlate(normalizedPlate)
                            .user(appointment.getUser())
                            .build()));
            appointment.setVehicleEntity(v);
            appointment.setVehicle(v.getBrand() + " " + v.getModel() + " " + v.getBuildYear());
        }

        Offer offer = null;
        if (dto.getOfferId() != null) {
            offer = offerRepository.findById(dto.getOfferId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Angebot nicht gefunden: " + dto.getOfferId()));
            appointment.setOffer(offer);
        }

        List<ServiceEntity> selectedServices = new ArrayList<>();
        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            selectedServices = dto.getServiceIds().stream()
                    .map(id -> serviceRepository.findById(id)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leistung nicht gefunden: " + id)))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else if (offer != null && offer.getServiceEntities() != null) {
            selectedServices = new ArrayList<>(offer.getServiceEntities());
        } else {
            Optional<ServiceEntity> serviceOpt = Optional.empty();
            if (dto.getServiceId() != null) {
                serviceOpt = serviceRepository.findById(dto.getServiceId());
            }
            if (serviceOpt.isEmpty() && dto.getServiceType() != null && !dto.getServiceType().isBlank()) {
                serviceOpt = serviceRepository.findByTitleIgnoreCase(dto.getServiceType());
            }
            serviceOpt.ifPresent(selectedServices::add);
        }

        if (!selectedServices.isEmpty()) {
            appointment.setServices(selectedServices);
            appointment.setServiceEntity(selectedServices.get(0));
            appointment.setServiceType(
                    selectedServices.stream().map(ServiceEntity::getTitle).collect(Collectors.joining(", "))
            );
        }

        int duration;
        if (offer != null && offer.getDuration() != null && offer.getDuration() > 0) {
            duration = offer.getDuration();
        } else if (!selectedServices.isEmpty()) {
            duration = selectedServices.stream()
                    .mapToInt(s -> s.getDuration() != null ? s.getDuration() : 0)
                    .sum();
            if (duration <= 0) duration = DEFAULT_DURATION_MINUTES;
        } else {
            duration = DEFAULT_DURATION_MINUTES;
        }
        appointment.setDurationMinutes(duration);

        if (appointment.getPrice() == null) {
            if (offer != null && offer.getPrice() != null) {
                appointment.setPrice(offer.getPrice());
            } else if (!selectedServices.isEmpty()) {
                appointment.setPrice(
                        selectedServices.stream().mapToDouble(s -> s.getPrice() != null ? s.getPrice() : 0).sum());
            } else {
                appointment.setPrice(0.0);
            }
        }

        validateSlotAvailability(appointment.getPreferredDate(), duration, null);

        appointment.setStatus(AppointmentStatus.NEU);
        Appointment saved = appointmentRepository.save(appointment);

        mailService.sendAppointmentConfirmation(saved);

        return appointmentMapper.toDto(saved);
    }

    private void validateSlotAvailability(LocalDateTime preferredDate, int duration, Long excludeAppointmentId) {
        if (preferredDate == null) return;

        LocalTime start = preferredDate.toLocalTime();
        LocalTime end = start.plusMinutes(duration);

        boolean overlap = appointmentRepository.getAllAppointments(preferredDate.toLocalDate()).stream()
                .filter(a -> excludeAppointmentId == null || !excludeAppointmentId.equals(a.getId()))
                .filter(a -> a.getStatus() != AppointmentStatus.ABGELEHNT)
                .anyMatch(a -> {
                    LocalTime otherStart = a.getPreferredDate().toLocalTime();
                    int otherDuration = a.getDurationMinutes() != null ? a.getDurationMinutes() : DEFAULT_DURATION_MINUTES;
                    LocalTime otherEnd = otherStart.plusMinutes(otherDuration);
                    return start.isBefore(otherEnd) && otherStart.isBefore(end);
                });

        if (overlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Der gewählte Termin überschneidet sich mit einem bereits gebuchten Termin. Bitte wählen Sie einen anderen Zeitpunkt.");
        }
    }

    @Transactional
    public AppointmentDTO updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Termin nicht gefunden."));

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(status);
        Appointment saved = appointmentRepository.save(appointment);

        if (status != AppointmentStatus.AUSSTEHEND && status != oldStatus) {
            mailService.sendAppointmentStatusUpdate(saved);
        }

        return appointmentMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Termin existiert nicht.");
        appointmentRepository.deleteById(id);
    }
}