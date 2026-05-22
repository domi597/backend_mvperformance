package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.TimeslotDTO;
import at.htlkaindorf.backend_mwperformence.entites.Timeslot;
import at.htlkaindorf.backend_mwperformence.mapper.TimeslotMapper;
import at.htlkaindorf.backend_mwperformence.repositories.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;

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

    public List<TimeslotDTO> getAll() {
        return timeslotMapper.toDto(
                timeslotRepository.findAll()
                        .stream()
                        .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
                        .toList()
        );
    }

    public TimeslotDTO create(LocalTime time) {
        if (timeslotRepository.existsByTime(time))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Timeslot bereits vorhanden: " + time);
        Timeslot saved = timeslotRepository.save(Timeslot.builder().time(time).build());
        return timeslotMapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!timeslotRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Timeslot nicht gefunden: " + id);
        timeslotRepository.deleteById(id);
    }
}
