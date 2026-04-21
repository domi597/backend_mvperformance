package at.htlkaindorf.backend_mwperformence.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 01.04.2026
 * Time: 21:24
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "opening_hours")
public class OpeningHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_label", nullable = false, length = 50)
    private String dayLabel;

    @Column(name = "open_time", length = 5)
    private String openTime;

    @Column(name = "close_time", length = 5)
    private String closeTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean closed = false;
}

