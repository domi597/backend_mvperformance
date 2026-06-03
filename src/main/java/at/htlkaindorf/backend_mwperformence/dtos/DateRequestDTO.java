package at.htlkaindorf.backend_mwperformence.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 03.06.2026
 * Time: 17:19
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateRequestDTO {
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;
}
