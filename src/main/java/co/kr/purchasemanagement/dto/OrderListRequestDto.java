package co.kr.purchasemanagement.dto;

import lombok.Data;

import java.util.function.Function;

@Data
public class OrderListRequestDto{
    Long productId;
    String productName;
    int quantity;
}
