package iuh.fit.reviewservice.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderResponse(
                UUID id,
                UUID customerId,
                OrderStatus status,
                List<OrderItemResponse> items) {
}
