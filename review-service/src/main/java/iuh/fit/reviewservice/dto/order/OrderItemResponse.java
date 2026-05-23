package iuh.fit.reviewservice.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderItemResponse(
                UUID id,
                UUID productVariantId) {
}
