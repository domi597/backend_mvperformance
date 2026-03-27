package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 11:10
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Sortiert nach Datum, damit die nächsten Termine oben stehen
    List<Appointment> findAllByOrderByPreferredDateAsc();

    // Hilfreich für dein Dashboard ("Status: Neu")
    List<Appointment> findByStatusIgnoreCase(String status);
}
