package co.kr.productservice.feign.orderTo.service;

import co.kr.productservice.feign.orderTo.dto.ProductResponseDto;
import co.kr.productservice.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderToService {
    private final ProductRepository productRepository;

    public ProductResponseDto getProduct(Long productId) {
        return productRepository.findById(productId)
                .map(productEntity -> new ProductResponseDto(productEntity))
                .orElse(new ProductResponseDto());
    }
}
