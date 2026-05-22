package at.htlkaindorf.backend_mwperformence.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 22.05.2026
 * Time: 11:01
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeslotDTO {
    private Long id;
    private LocalTime time;
}
