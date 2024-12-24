package co.kr.purchasemanagement.product.controller;

import co.kr.purchasemanagement.product.entity.ProductResponseDto;
import co.kr.purchasemanagement.product.entity.ProductEntity;
import co.kr.purchasemanagement.order.service.OrderService;
import co.kr.purchasemanagement.product.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final OrderService orderService;

    // 상품 리스트
    @GetMapping
    public List<ProductResponseDto> getList(@RequestParam int size,
                                            @RequestParam int page) {
        return productService.getList(size, page);
    }

    // 상품 상세 ( product list or wish List )
    @GetMapping("/{productId}")
    public ProductEntity getProduct(@PathVariable Long productId) {
        return productService.getProduct(productId);
    }



}
