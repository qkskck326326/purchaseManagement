package co.kr.productservice.feign.orderTo.dto;

import co.kr.productservice.product.entity.ProductEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponseDto {
    Long productId;
    String productName;

    public ProductResponseDto(ProductEntity productEntity) {
        this.productId = productEntity.getProductId();
        this.productName = productEntity.getProductName();
    }
}