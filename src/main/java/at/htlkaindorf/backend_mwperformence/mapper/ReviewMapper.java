package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.ReviewDTO;
import at.htlkaindorf.backend_mwperformence.entites.Review;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 25.03.2026
 * Time: 12:53
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review toEntity(ReviewDTO reviewDTO);

    List<Review> toEntity(List<ReviewDTO> reviewDTO);

    ReviewDTO toDto(Review review);

    List<ReviewDTO> toDto(List<Review> review);
}

