package co.kr.orderservice.feign.toProduct;

import lombok.Data;

@Data
public class ProductResponseDto {
    Long productId;
    String productName;
}
