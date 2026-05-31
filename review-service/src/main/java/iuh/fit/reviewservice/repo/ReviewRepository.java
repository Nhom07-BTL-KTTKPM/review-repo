package iuh.fit.reviewservice.repo;

import iuh.fit.reviewservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    interface ReviewStatsProjection {
        Double getAverageRating();

        Long getTotalReviews();
    }

    Page<Review> findByProductIdAndIsActiveTrueOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    List<Review> findByCustomerId(UUID customerId);

    boolean existsByOrderItemId(UUID orderItemId);

    @Query("select avg(r.rating) as averageRating, count(r) as totalReviews from Review r " +
            "where r.productId = :productId and r.isActive = true")
    ReviewStatsProjection getReviewStatsByProductId(@Param("productId") UUID productId);
}
