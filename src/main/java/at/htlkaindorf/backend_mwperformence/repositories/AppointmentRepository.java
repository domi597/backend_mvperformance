package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 11:10
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.status = :status")
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.status NOT IN :statuses")
    Page<Appointment> findByStatusNotIn(List<AppointmentStatus> statuses, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.preferredDate BETWEEN :start AND :end")
    Page<Appointment> findByPreferredDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE CAST(a.preferredDate AS date) = :date")
    List<Appointment> getAllAppointments(LocalDate date);

    List<Appointment> findByStatusAndCreatedAtBefore(AppointmentStatus status, LocalDateTime date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.user.id = :userId AND a.status IN :statuses")
    long countByUserIdAndStatusIn(Long userId, List<AppointmentStatus> statuses);

    List<Appointment> findByUserId(Long userId);
}