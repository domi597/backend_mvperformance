package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.ReviewSummaryDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class ReviewService {

    /** Google-Place-ID des Unternehmenseintrags "KFZ-Technik-GDG" (Kaindorf an der Sulm / Leibnitz). */
    @Value("${google.places.place-id}")
    private String placeId;

    /** API-Key für die Places API (New). Wenn leer, wird nur mit den Fallback-Werten unten gearbeitet. */
    @Value("${google.places.api-key:}")
    private String apiKey;

    /**
     * Fallback / zuletzt bekannter Stand, falls noch kein API-Key hinterlegt ist oder der Abruf fehlschlägt.
     * Kommt aus google.places.fallback-rating (.env: GOOGLE_PLACES_FALLBACK_RATING) – zum Aktualisieren
     * reicht es, den Wert in der .env zu ändern und das Backend neu zu starten (kein Rebuild nötig).
     */
    @Value("${google.places.fallback-rating}")
    private double fallbackRating;

    /** Siehe fallbackRating – .env: GOOGLE_PLACES_FALLBACK_REVIEW_COUNT. */
    @Value("${google.places.fallback-review-count}")
    private int fallbackReviewCount;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://places.googleapis.com/v1")
            .build();

    private volatile double cachedRating;
    private volatile int cachedReviewCount;

    @PostConstruct
    public void init() {
        cachedRating = fallbackRating;
        cachedReviewCount = fallbackReviewCount;
        refresh();
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void refresh() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("google.places.api-key (GOOGLE_PLACES_API_KEY) ist nicht gesetzt – " +
                    "Bewertungen werden mit den zuletzt hinterlegten Werten ({} Sterne, {} Rezensionen) ausgeliefert.",
                    cachedRating, cachedReviewCount);
            return;
        }

        try {
            GooglePlaceResponse response = restClient.get()
                    .uri("/places/{placeId}", placeId)
                    .header("X-Goog-Api-Key", apiKey)
                    .header("X-Goog-FieldMask", "rating,userRatingCount")
                    .retrieve()
                    .body(GooglePlaceResponse.class);

            if (response != null && response.rating() != null && response.userRatingCount() != null) {
                cachedRating = response.rating();
                cachedReviewCount = response.userRatingCount();
                log.info("Google-Bewertungen aktualisiert: {} Sterne, {} Rezensionen.", cachedRating, cachedReviewCount);
            }
        } catch (Exception e) {
            log.warn("Google-Bewertungen konnten nicht aktualisiert werden, verwende weiterhin die zuletzt " +
                    "bekannten Werte ({} Sterne, {} Rezensionen). Grund: {}", cachedRating, cachedReviewCount, e.getMessage());
        }
    }

    public ReviewSummaryDTO getSummary() {
        return ReviewSummaryDTO.builder()
                .rating(cachedRating)
                .reviewCount(cachedReviewCount)
                .reviewsUrl("https://search.google.com/local/reviews?placeid=" + placeId)
                .build();
    }

    /** Schlankes Hilfsrecord nur zum Parsen der Places-API-Antwort (rating + userRatingCount). */
    private record GooglePlaceResponse(Double rating, Integer userRatingCount) {}
}
