package at.htlkaindorf.backend_mwperformence.repositories;

import at.htlkaindorf.backend_mwperformence.entites.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 25.03.2026
 * Time: 12:35
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Neueste zuerst
    List<Review> findAllByOrderByCreatedAtDesc();

    // Nur Bewertungen mit bestimmter Sternanzahl
    @Query("SELECT r FROM Review r WHERE r.stars = :stars ORDER BY r.createdAt DESC")
    List<Review> findAllByStars(Short stars);

    // Anzahl der Bewertungen mit mind. X Sternen
    @Query("SELECT COUNT(r) FROM Review r WHERE r.stars >= :minStars")
    Long countReviewsWithMinStars(Short minStars);
}
