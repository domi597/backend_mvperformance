package at.htlkaindorf.backend_mwperformence.scheduler;

import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.repositories.AppointmentRepository;
import at.htlkaindorf.backend_mwperformence.services.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 03.06.2026
 * Time: 19:11
 */


@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;
    private final MailService mailService;

    /** Status, die als final gelten und daher nicht mehr auf "nicht mehr machbar" geprüft werden. */
    private static final List<AppointmentStatus> FINAL_STATES = List.of(
            AppointmentStatus.ABGESCHLOSSEN, AppointmentStatus.ABGELEHNT, AppointmentStatus.STORNIERT
    );

    @Scheduled(cron = "0 0 0 * * *")
    public void autoSetAusstehend() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        List<Appointment> toUpdate = appointmentRepository
                .findByStatusAndCreatedAtBefore(AppointmentStatus.NEU, cutoff);

        toUpdate.forEach(a -> a.setStatus(AppointmentStatus.AUSSTEHEND));
        appointmentRepository.saveAll(toUpdate);
    }

    /** Grund, der bei automatischer Ablehnung wegen Zeitüberschreitung im rejectionReason-Feld gespeichert wird. */
    private static final String EXPIRED_REJECTION_REASON =
            "Automatisch abgelehnt: Der gewünschte Termin ist verstrichen, ohne rechtzeitig bearbeitet worden zu sein.";

    @Scheduled(cron = "0 * * * * *")
    public void autoRejectExpiredAppointments() {
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> expired = appointmentRepository
                .findByPreferredDateBeforeAndStatusNotIn(now, FINAL_STATES);

        for (Appointment appointment : expired) {
            appointment.setStatus(AppointmentStatus.ABGELEHNT);
            appointment.setRejectionReason(EXPIRED_REJECTION_REASON);
            appointmentRepository.save(appointment);
            mailService.sendAppointmentExpiredRejection(appointment);
        }

        if (!expired.isEmpty()) {
            log.info("{} nicht mehr machbare(r) Termin(e) automatisch auf ABGELEHNT gesetzt.", expired.size());
        }
    }
}