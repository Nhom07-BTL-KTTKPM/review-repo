package iuh.fit.reviewservice.service.impl;

import iuh.fit.reviewservice.client.CatalogServiceClient;
import iuh.fit.reviewservice.client.OrderServiceClient;
import iuh.fit.reviewservice.dto.ReviewRequest;
import iuh.fit.reviewservice.dto.ReviewResponse;
import iuh.fit.reviewservice.dto.catalog.ProductVariantResponse;
import iuh.fit.reviewservice.dto.order.OrderItemResponse;
import iuh.fit.reviewservice.dto.order.OrderResponse;
import iuh.fit.reviewservice.dto.order.OrderStatus;
import iuh.fit.reviewservice.entity.Review;
import iuh.fit.reviewservice.repo.ReviewRepository;
import iuh.fit.reviewservice.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iuh.fit.reviewservice.client.UserServiceClient;
import iuh.fit.reviewservice.dto.user.CustomerResponse;
import iuh.fit.shared.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderServiceClient orderServiceClient;
    private final CatalogServiceClient catalogServiceClient;
    private final UserServiceClient userServiceClient;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            OrderServiceClient orderServiceClient,
            CatalogServiceClient catalogServiceClient,
            UserServiceClient userServiceClient) {
        this.reviewRepository = reviewRepository;
        this.orderServiceClient = orderServiceClient;
        this.catalogServiceClient = catalogServiceClient;
        this.userServiceClient = userServiceClient;
    }

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        validateRequest(request);

        if (reviewRepository.existsByOrderItemId(request.getOrderItemId())) {
            throw new IllegalStateException("Order item already reviewed");
        }

        OrderItemResponse orderItem = findDeliveredOrderItem(request.getCustomerId(), request.getOrderItemId());

        // Resolve the actual productId from the order item's variant
        ProductVariantResponse variant = catalogServiceClient.getVariantById(orderItem.productVariantId());
        UUID resolvedProductId = variant.getProductId();

        // If frontend provided a productId, validate it matches; otherwise use the resolved one
        if (request.getProductId() != null && !request.getProductId().equals(resolvedProductId)) {
            log.warn("Provided productId={} does not match resolved productId={} from variant={}",
                    request.getProductId(), resolvedProductId, orderItem.productVariantId());
            throw new IllegalArgumentException("Order item does not match product");
        }

        Review review = new Review();
        review.setProductId(resolvedProductId);
        review.setCustomerId(request.getCustomerId());
        review.setOrderItemId(request.getOrderItemId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setImageUrls(request.getImageUrls() == null ? List.of() : request.getImageUrls());
        review.setIsActive(true);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getReviewsByProductId(UUID productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndIsActiveTrueOrderByCreatedAtDesc(productId, pageable)
                .map(this::mapToResponse);
    }

    private void validateRequest(ReviewRequest request) {
        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer id is required");
        }
        if (request.getOrderItemId() == null) {
            throw new IllegalArgumentException("Order item id is required");
        }
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    private OrderItemResponse findDeliveredOrderItem(UUID customerId, UUID orderItemId) {
        List<OrderResponse> orders = orderServiceClient.getOrdersByCustomerId(customerId.toString());
        log.info("Fetched {} orders for customer {}", orders == null ? 0 : orders.size(), customerId);
        if (orders != null) {
            for (OrderResponse o : orders) {
                String items = safeItems(o).stream().map(it -> String.valueOf(it.id())).toList().toString();
                log.info("Order id={}, status={}, items={}", o.id(), o.status(), items);
            }
        }

        Optional<OrderItemResponse> item = orders == null ? Optional.empty()
                : orders.stream()
                        .filter(order -> order.status() != null && "DELIVERED".equalsIgnoreCase(order.status().name()))
                        .flatMap(order -> safeItems(order).stream())
                        .filter(orderItem -> orderItem.id() != null && orderItem.id().equals(orderItemId))
                        .findFirst();

        if (item.isEmpty()) {
            log.warn("No delivered order item found for customer={} orderItemId={}. Orders fetched={}", customerId,
                    orderItemId, orders == null ? 0 : orders.size());
            throw new IllegalStateException("Only delivered orders can be reviewed");
        }

        return item.get();
    }

    private void validateProductMatch(OrderItemResponse orderItem, UUID productId) {
        ProductVariantResponse variant = catalogServiceClient.getVariantById(orderItem.productVariantId());
        if (!productId.equals(variant.getProductId())) {
            throw new IllegalArgumentException("Order item does not match product");
        }
    }

    private List<OrderItemResponse> safeItems(OrderResponse order) {
        if (order.items() == null) {
            return Collections.emptyList();
        }
        return order.items();
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setProductId(review.getProductId());
        response.setCustomerId(review.getCustomerId());
        
        try {
            ApiResponse<CustomerResponse> apiResponse = userServiceClient.getCustomerById(review.getCustomerId().toString());
            if (apiResponse != null && apiResponse.data() != null) {
                response.setCustomerName(apiResponse.data().getFullName());
            } else {
                response.setCustomerName("Người dùng Ẩn danh");
            }
        } catch (Exception e) {
            log.error("Failed to fetch customer name for customerId: {}", review.getCustomerId(), e);
            response.setCustomerName("Người dùng Ẩn danh");
        }

        response.setOrderItemId(review.getOrderItemId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setImageUrls(review.getImageUrls());
        response.setIsActive(review.getIsActive());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }
}
