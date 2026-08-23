package at.htlkaindorf.backend_mwperformence.dtos;

import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import lombok.Data;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 14.04.2026
 * Time: 09:16
 */

@Data
public class StatusUpdateRequest {
    private AppointmentStatus status;

    /** Pflichtfeld, wenn status == ABGELEHNT. Wird dem Kunden in der Absage-Mail angezeigt. */
    private String reason;
}
