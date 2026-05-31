package iuh.fit.reviewservice.client;

import iuh.fit.reviewservice.dto.catalog.ProductRatingUpdateRequest;
import iuh.fit.reviewservice.dto.catalog.ProductVariantResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface CatalogServiceClient {

    @GetMapping("/api/v1/catalog/variants/{variantId}")
    ProductVariantResponse getVariantById(@PathVariable("variantId") UUID variantId);

    @PostMapping("/api/v1/catalog/products/ratings/update")
    void updateProductRatingStats(@RequestBody ProductRatingUpdateRequest request);
}
