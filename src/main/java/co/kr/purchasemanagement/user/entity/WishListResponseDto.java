package co.kr.purchasemanagement.user.entity;

import co.kr.purchasemanagement.order.entity.WishListEntity;

public class WishListResponseDto {
    Long ProductDto;
    String ProductName;

    public WishListResponseDto(WishListEntity wishList) {
        this.ProductDto = wishList.getProductId();
        this.ProductName = wishList.getProductName();
    }
}
