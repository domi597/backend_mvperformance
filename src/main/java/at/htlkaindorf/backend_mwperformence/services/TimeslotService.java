package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.DateRequestDTO;
import at.htlkaindorf.backend_mwperformence.dtos.TimeslotDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.entites.Timeslot;
import at.htlkaindorf.backend_mwperformence.mapper.TimeslotMapper;
import at.htlkaindorf.backend_mwperformence.repositories.AppointmentRepository;
import at.htlkaindorf.backend_mwperformence.repositories.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 22.05.2026
 * Time: 11:05
 */


@Service
@RequiredArgsConstructor
public class TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final TimeslotMapper timeslotMapper;
    private final AppointmentRepository appointmentRepository;

    public List<TimeslotDTO> getAll() {
        return timeslotMapper.toDto(
                timeslotRepository.findAll()
                        .stream()
                        .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
                        .toList()
        );
    }

    public List<TimeslotDTO> getAvailable(LocalDate date, Integer durationMinutes) {
        int duration = (durationMinutes != null && durationMinutes > 0) ? durationMinutes : 30;

        List<Timeslot> allSlots = timeslotRepository.findAll().stream()
                .sorted(Comparator.comparing(Timeslot::getTime))
                .toList();

        if (allSlots.isEmpty()) {
            return List.of();
        }

        Set<LocalTime> slotTimes = allSlots.stream().map(Timeslot::getTime).collect(Collectors.toSet());
        int gridStep = gridStepMinutes(allSlots);
        LocalTime lastGridTime = allSlots.get(allSlots.size() - 1).getTime();

        List<LocalTime[]> occupiedRanges = appointmentRepository.getAllAppointments(date).stream()
                .filter(a -> a.getStatus() != AppointmentStatus.ABGELEHNT)
                .map(a -> {
                    LocalTime start = a.getPreferredDate().toLocalTime();
                    int appointmentDuration = a.getDurationMinutes() != null ? a.getDurationMinutes() : gridStep;
                    return new LocalTime[]{start, start.plusMinutes(appointmentDuration)};
                })
                .toList();

        List<Timeslot> available = allSlots.stream()
                .filter(slot -> {
                    LocalTime start = slot.getTime();
                    LocalTime end = start.plusMinutes(duration);

                    if (end.isAfter(lastGridTime.plusMinutes(gridStep))) {
                        return false;
                    }

                    LocalTime cursor = start.plusMinutes(gridStep);
                    while (cursor.isBefore(end)) {
                        if (!slotTimes.contains(cursor)) {
                            return false;
                        }
                        cursor = cursor.plusMinutes(gridStep);
                    }

                    return occupiedRanges.stream().noneMatch(occ ->
                            start.isBefore(occ[1]) && occ[0].isBefore(end));
                })
                .toList();

        return timeslotMapper.toDto(available);
    }

    private int gridStepMinutes(List<Timeslot> sortedSlots) {
        int step = 30;
        int minStep = Integer.MAX_VALUE;
        for (int i = 1; i < sortedSlots.size(); i++) {
            int diff = (int) java.time.Duration.between(sortedSlots.get(i - 1).getTime(), sortedSlots.get(i).getTime()).toMinutes();
            if (diff > 0 && diff < minStep) {
                minStep = diff;
            }
        }
        return minStep == Integer.MAX_VALUE ? step : minStep;
    }

    public void delete(Long id) {
        if (!timeslotRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Timeslot nicht gefunden: " + id);
        timeslotRepository.deleteById(id);
    }
}