package co.kr.productservice.feign.orderTo.controller;

import co.kr.productservice.feign.orderTo.dto.ProductResponseDto;
import co.kr.productservice.feign.orderTo.service.OrderToService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/order/to/product")
@AllArgsConstructor
public class OrderToController {
    private final OrderToService orderToService;

    @GetMapping
    public ProductResponseDto getProduct(@RequestParam("productId") Long productId) {
        return orderToService.getProduct(productId);
    }
}
