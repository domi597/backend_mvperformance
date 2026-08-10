package at.htlkaindorf.backend_mwperformence.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Ein von der Werkstatt gesperrter Zeitraum, in dem keine Termine gebucht
 * werden können (z. B. Betriebsurlaub, Feiertage oder Mittagspause).
 * <p>
 * Sind {@link #startTime} bzw. {@link #endTime} nicht gesetzt, gilt die
 * Sperre ganztägig für jeden Tag zwischen {@link #startDate} und
 * {@link #endDate}. Sind beide gesetzt, gilt die Sperre nur für dieses
 * Zeitfenster an genau einem Tag ({@code startDate == endDate}).
 *
 * Project: backend_MWPerformence
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "blocked_periods")
public class BlockedPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(length = 255)
    private String reason;
}
