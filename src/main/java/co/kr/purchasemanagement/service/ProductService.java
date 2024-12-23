package co.kr.purchasemanagement.service;

import co.kr.purchasemanagement.dto.ProductResponseDto;
import co.kr.purchasemanagement.entity.ProductEntity;
import co.kr.purchasemanagement.repository.ProductRepository;
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
