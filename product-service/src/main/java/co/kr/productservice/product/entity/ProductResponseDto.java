package co.kr.productservice.product.entity;

public class ProductResponseDto {
    private Long productId;
    private String productName;
    private int productQuantity;
    private int likeCount;

    public ProductResponseDto(ProductEntity productEntity) {
        this.productId = productEntity.getProductId();
        this.productName = productEntity.getProductName();
        this.productQuantity = productEntity.getProductQuantity();
        this.likeCount = productEntity.getLikeCount();
    }
}
