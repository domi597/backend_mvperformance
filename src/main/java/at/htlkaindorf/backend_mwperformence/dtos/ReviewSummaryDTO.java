package at.htlkaindorf.backend_mwperformence.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger (KI)
 * Date: 24.08.2026
 *
 * Zusammenfassung der Google-Bewertungen (Sternedurchschnitt + Anzahl),
 * wird periodisch im Hintergrund aktualisiert, siehe {@link at.htlkaindorf.backend_mwperformence.services.ReviewService}.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewSummaryDTO {
    private Double rating;
    private Integer reviewCount;
    private String reviewsUrl;
}
