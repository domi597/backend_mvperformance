package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.ReviewDTO;
import at.htlkaindorf.backend_mwperformence.entites.Review;
import at.htlkaindorf.backend_mwperformence.mapper.ReviewMapper;
import at.htlkaindorf.backend_mwperformence.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 09:48
 */


@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @GetMapping
    public List<ReviewDTO> getAll() {
        return reviewMapper.toDto(
                reviewRepository.findAllByOrderByCreatedAtDesc()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getById(@PathVariable Long id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stars/{stars}")
    public List<ReviewDTO> getByStars(@PathVariable Short stars) {
        return reviewMapper.toDto(
                reviewRepository.findAllByStars(stars)
        );
    }

    @GetMapping("/count")
    public Long countByMinStars(@RequestParam Short minStars) {
        return reviewRepository.countReviewsWithMinStars(minStars);
    }

    @PostMapping
    public ReviewDTO create(@RequestBody ReviewDTO reviewDTO) {
        Review saved = reviewRepository.save(
                reviewMapper.toEntity(reviewDTO)
        );
        return reviewMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> update(@PathVariable Long id,
                                            @RequestBody ReviewDTO updated) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setStars(updated.getStars());
                    existing.setText(updated.getText());
                    return ResponseEntity.ok(
                            reviewMapper.toDto(reviewRepository.save(existing))
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
