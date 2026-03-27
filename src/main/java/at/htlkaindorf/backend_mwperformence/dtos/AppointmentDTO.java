package at.htlkaindorf.backend_mwperformence.dtos;

import lombok.Data;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 11:11
 */

@Data
public class AppointmentDTO {
    private Long id;
    private String customerName;
    private String serviceType;
    private String vehicle;
    private String preferredDate;
    private String status;
}
