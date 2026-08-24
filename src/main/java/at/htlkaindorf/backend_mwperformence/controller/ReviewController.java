package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.ReviewSummaryDTO;
import at.htlkaindorf.backend_mwperformence.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger (KI)
 * Date: 27.03.2026
 * Time: 09:48
 */


@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /** Öffentlich (siehe SecurityConfig: /api/reviews/** ist permitAll) — Sterne-Schnitt + Anzahl für Startseite/Kontaktseite. */
    @GetMapping("/summary")
    public ResponseEntity<ReviewSummaryDTO> getSummary() {
        return ResponseEntity.ok(reviewService.getSummary());
    }
}
