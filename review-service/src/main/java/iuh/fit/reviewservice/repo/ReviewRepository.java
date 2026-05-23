package iuh.fit.reviewservice.repo;

import iuh.fit.reviewservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByProductIdAndIsActiveTrueOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    boolean existsByOrderItemId(UUID orderItemId);
}
