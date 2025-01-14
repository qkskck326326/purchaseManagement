package co.kr.orderservice.order.controller;


import co.kr.orderservice.order.entity.OrderItemRequestDto;
import co.kr.orderservice.order.entity.ProductOrderEntity;
import co.kr.orderservice.order.entity.WishListResponseDto;
import co.kr.orderservice.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 위시리스트 보기
    @GetMapping("/wishList")
    public List<WishListResponseDto> getWishList(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.getWishList(bearerToken);
    }

    // 위시리스트에 해당 상품 추가
    @PostMapping("/wishList/{productId}")
    public String addWishList(@PathVariable Long productId, @RequestParam int quantity, @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.addWishList(productId, bearerToken, quantity);
    }

    // 위시리스트 수량 수정
    @GetMapping("/wishList/edit/quantity")
    public String editWishListQuantity(@RequestParam Long wishListId, @RequestParam int quantity, @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.editQuantityWishList(wishListId, quantity, bearerToken);
    }

    // 위시리스트 삭제
    @GetMapping("/wishList/delete/{wishListId}")
    public String deleteWishListQuantity(@PathVariable Long wishListId, @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.deleteQuantityWishList(wishListId, bearerToken);
    }

    // 상품 주문
    @PostMapping("/products")
    public String orderProducts(@RequestBody List<OrderItemRequestDto> orderList,
                                @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.orderProducts(orderList, bearerToken);
    }

    // 주문 결제 완료 - 수정점 - 추수 결제 여부가 확인되는 인증코드등 추가 필요할 수 있음
    @PostMapping("/products/payed/{orderId}")
    public String orderProductsPayed(@PathVariable Long orderId, @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken){
        return orderService.orderProductsPayed(orderId, bearerToken);
    }

    // 주문 취소
    @PostMapping("/cancellation")
    public String orderCancellation(@RequestParam Long orderId,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.OrderCancellation(orderId, bearerToken);
    }

    // 반품신청
    @PostMapping("/refund")
    public String orderRefund(@RequestParam Long orderId,
                              @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.orderRefund(orderId, bearerToken);
    }

    //////////////////////////////////

    // 주문 정보 목록 API
    @GetMapping("/show")
    public List<ProductOrderEntity> showOrderList(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.showOrderList(bearerToken);
    }

    // 주문 정보 API
    @GetMapping("/show/{orderId}")
    public ProductOrderEntity showOrder(@PathVariable Long orderId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return orderService.showOrder(orderId, bearerToken);
    }



}
