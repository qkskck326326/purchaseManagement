package co.kr.orderservice.order.entity;

import co.kr.orderservice.order.entity.WishListEntity;

public class WishListResponseDto {
    Long ProductDto;
    String ProductName;

    public WishListResponseDto(WishListEntity wishList) {
        this.ProductDto = wishList.getProductId();
        this.ProductName = wishList.getProductName();
    }
}
