package at.htlkaindorf.backend_mwperformence.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 02.04.2026
 * Time: 23:05
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferDTO {
    private Long id;
    private String icon;
    private String title;
    private String description;
    private Double price;
    private Integer duration;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<ServiceEntityDTO> services;
}