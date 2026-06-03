package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.dtos.TimeslotDTO;
import at.htlkaindorf.backend_mwperformence.entites.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 22.05.2026
 * Time: 11:03
 */

public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {

}