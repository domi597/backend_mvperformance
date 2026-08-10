package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.BlockedPeriodDTO;
import at.htlkaindorf.backend_mwperformence.entites.BlockedPeriod;
import at.htlkaindorf.backend_mwperformence.mapper.BlockedPeriodMapper;
import at.htlkaindorf.backend_mwperformence.repositories.BlockedPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * <p>
 * Verwaltet die von der Werkstatt festgelegten Sperrzeiträume. Das eigentliche
 * Herausfiltern gesperrter Slots aus der Verfügbarkeitsberechnung übernimmt
 * {@link TimeslotService}, damit ein gesperrter Slot dort gar nicht erst als
 * frei zurückgegeben wird.
 */
@Service
@RequiredArgsConstructor
public class BlockedPeriodService {

    private final BlockedPeriodRepository blockedPeriodRepository;
    private final BlockedPeriodMapper blockedPeriodMapper;

    public List<BlockedPeriodDTO> getAll() {
        return blockedPeriodMapper.toDto(blockedPeriodRepository.findAllByOrderByStartDateAsc());
    }

    public BlockedPeriodDTO create(BlockedPeriodDTO dto) {
        validate(dto);

        BlockedPeriod entity = BlockedPeriod.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .reason(dto.getReason() != null && !dto.getReason().isBlank() ? dto.getReason().trim() : null)
                .build();

        return blockedPeriodMapper.toDto(blockedPeriodRepository.save(entity));
    }

    public void delete(Long id) {
        if (!blockedPeriodRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sperrzeit nicht gefunden: " + id);
        }
        blockedPeriodRepository.deleteById(id);
    }

    /**
     * Validiert die fachlichen Regeln aus dem Frontend-Kommentar
     * (src/api/blockedPeriodApi.ts) auch serverseitig, da das Frontend als
     * Absicherung nicht ausreicht.
     */
    private void validate(BlockedPeriodDTO dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start- und Enddatum müssen gesetzt sein.");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Das Enddatum darf nicht vor dem Startdatum liegen.");
        }

        boolean hasStartTime = dto.getStartTime() != null;
        boolean hasEndTime = dto.getEndTime() != null;

        if (hasStartTime != hasEndTime) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start- und Endzeit müssen gemeinsam gesetzt oder beide leer sein.");
        }

        if (hasStartTime) {
            if (!dto.getEndTime().isAfter(dto.getStartTime())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Endzeit muss nach der Startzeit liegen.");
            }
            if (!dto.getStartDate().isEqual(dto.getEndDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Eine Uhrzeit-Sperre gilt nur für einen einzelnen Tag (startDate == endDate).");
            }
        }
    }
}
