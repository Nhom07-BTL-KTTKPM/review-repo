package iuh.fit.reviewservice.dto.catalog;

import java.util.UUID;

public class ProductVariantResponse {

    private UUID id;
    private UUID productId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
