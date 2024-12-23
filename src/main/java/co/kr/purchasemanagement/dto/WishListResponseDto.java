package co.kr.purchasemanagement.dto;

import co.kr.purchasemanagement.entity.WishListEntity;

public class WishListResponseDto {
    Long ProductDto;
    String ProductName;

    public WishListResponseDto(WishListEntity wishList) {
        this.ProductDto = wishList.getProductId();
        this.ProductName = wishList.getProductName();
    }
}
