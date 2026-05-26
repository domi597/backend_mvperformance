package at.htlkaindorf.backend_mwperformence.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 10:21
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceEntityDTO {
    private Long id;
    private String icon;
    private String title;
    private String subtitle;
    private Double price;
}