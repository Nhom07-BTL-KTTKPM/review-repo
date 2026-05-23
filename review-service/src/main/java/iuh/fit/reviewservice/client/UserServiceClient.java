package iuh.fit.reviewservice.client;

import iuh.fit.reviewservice.dto.user.CustomerResponse;
import iuh.fit.shared.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/v1/user/customers/{customerId}")
    ApiResponse<CustomerResponse> getCustomerById(@PathVariable("customerId") String customerId);
}
