package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 22.05.2026
 * Time: 11:03
 */

public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
    boolean existsByTime(java.time.LocalTime time);
}