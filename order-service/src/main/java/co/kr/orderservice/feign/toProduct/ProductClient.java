package co.kr.orderservice.feign.toProduct;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {
    @GetMapping("/feign/order/to/product")
    ProductResponseDto getProduct(@RequestParam("productId") Long productId);
}
