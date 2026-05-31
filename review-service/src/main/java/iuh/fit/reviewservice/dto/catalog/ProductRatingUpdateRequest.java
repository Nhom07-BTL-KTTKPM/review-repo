package iuh.fit.reviewservice.dto.catalog;

import java.util.UUID;

public class ProductRatingUpdateRequest {

    private UUID productId;
    private Double averageRating;
    private Integer totalReviews;

    public ProductRatingUpdateRequest() {
    }

    public ProductRatingUpdateRequest(UUID productId, Double averageRating, Integer totalReviews) {
        this.productId = productId;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
}
