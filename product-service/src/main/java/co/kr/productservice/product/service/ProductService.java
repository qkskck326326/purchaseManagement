package co.kr.productservice.product.service;


import co.kr.productservice.product.entity.ProductEntity;
import co.kr.productservice.product.entity.ProductResponseDto;
import co.kr.productservice.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProductService {
    private ProductRepository productRepository;

    public List<ProductResponseDto> getList(int size, int page) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ProductEntity> list = productRepository.findAll(pageRequest).getContent();

        return list.stream()
                .map(ProductResponseDto::new)
                .toList();
    }

    public ProductEntity getProduct(Long productId) {
        return productRepository.findById(productId).orElseGet(ProductEntity::new);
    }


}
