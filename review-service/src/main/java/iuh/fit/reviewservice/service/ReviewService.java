package iuh.fit.reviewservice.service;

import iuh.fit.reviewservice.dto.ReviewRequest;
import iuh.fit.reviewservice.dto.ReviewResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    Page<ReviewResponse> getReviewsByProductId(UUID productId, Pageable pageable);

    List<ReviewResponse> getReviewsByCustomerId(UUID customerId);

    ReviewResponse updateReview(UUID reviewId, ReviewRequest request);
}
