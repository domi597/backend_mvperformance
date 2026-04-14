package at.htlkaindorf.backend_mwperformence.dtos;

import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 11:11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private String serviceType;
    private String date;          // "YYYY-MM-DD"
    private String time;          // "HH:mm"
    private String brand;
    private String model;
    private Integer year;
    private String licensePlate;
    private AppointmentStatus status;
    private Double price;
    private String note;
    private String createdAt;     // ISO-8601 String
}