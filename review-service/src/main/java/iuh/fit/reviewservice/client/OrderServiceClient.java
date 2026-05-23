package iuh.fit.reviewservice.client;

import iuh.fit.reviewservice.dto.order.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/v1/orders/customer/{customerId}")
    List<OrderResponse> getOrdersByCustomerId(@PathVariable("customerId") String customerId);
}
