package at.htlkaindorf.backend_mwperformence.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 22.05.2026
 * Time: 10:59
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "timeslots")
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalTime time;

}