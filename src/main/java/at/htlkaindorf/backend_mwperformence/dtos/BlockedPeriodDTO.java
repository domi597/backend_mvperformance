package at.htlkaindorf.backend_mwperformence.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Project: backend_MWPerformence
 * <p>
 * Entspricht 1:1 dem Frontend-Interface {@code IBlockedPeriod}
 * (src/api/blockedPeriodApi.ts): startDate/endDate als "YYYY-MM-DD",
 * startTime/endTime optional als "HH:mm".
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockedPeriodDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String reason;
}
