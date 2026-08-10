package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.BlockedPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Project: backend_MWPerformence
 */
public interface BlockedPeriodRepository extends JpaRepository<BlockedPeriod, Long> {

    List<BlockedPeriod> findAllByOrderByStartDateAsc();

    /**
     * Liefert alle Sperrzeiträume, deren Bereich [startDate, endDate] das
     * übergebene Datum enthält. Wird von {@code TimeslotService} genutzt, um
     * bei der Slot-Berechnung ganztägige und Uhrzeit-Sperren zu berücksichtigen.
     */
    @Query("SELECT b FROM BlockedPeriod b WHERE b.startDate <= :date AND b.endDate >= :date")
    List<BlockedPeriod> findByDate(LocalDate date);
}
