package at.htlkaindorf.backend_mwperformence.dtos;

import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing an appointment.
 * <p>
 * Used to transfer appointment data between the service layer and the REST API
 * without exposing the internal {@link at.htlkaindorf.backend_mwperformence.entites.Appointment} entity directly.
 * </p>
 *
 * @author Dominik Ranegger
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDTO {
    /** Unique identifier of the appointment. */
    private Long id;
    /** Full name of the customer who booked the appointment. */
    private String customerName;
    /** Type of service requested (e.g. "Oil change"). */
    private String serviceType;
    /** Vehicle description associated with the appointment. */
    private String vehicle;
    /** The customer's preferred date and time for the appointment. */
    private LocalDateTime preferredDate;
    /** Current processing status of the appointment. */
    private AppointmentStatus status;
}
