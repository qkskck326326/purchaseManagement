package co.kr.purchasemanagement.controller;

import co.kr.purchasemanagement.dto.OrderListRequestDto;
import co.kr.purchasemanagement.dto.WishListResponseDto;
import co.kr.purchasemanagement.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 위시리스트에 해당 상품 추가  /// 편집점 - 추후 토큰에서 userEmail 가져오는 형식으로 변경해야됨
    @PostMapping("/wishList/{productId}")
    public String addWishList(@PathVariable Long productId, @RequestParam String userEmail, @RequestParam int quantity) {
        return orderService.addWishList(productId, userEmail, quantity);
    }

    // 위시리스트 보기  /// 편집점 - 추후 토큰에서 userEmail 가져오는 형식으로 변경해야됨
    @GetMapping("/wishList")
    public List<WishListResponseDto> getWishList(@RequestParam String userEmail) {
        return orderService.getWishList(userEmail);
    }

    // 위시리스트 수정
    @GetMapping("/wishList/edit/quantity")
    public String editWishListQuantity(@RequestParam Long wishListId, @RequestParam int quantity) {
        return orderService.editQuantityWishList(wishListId, quantity);
    }

    // 위시리스트 삭제
    @GetMapping("/wishList/delete/{wishListId}")
    public String deleteWishListQuantity(@PathVariable Long wishListId) {
        return orderService.deleteQuantityWishList(wishListId);
    }

    // 상품 주문 /// 편집점 - 추후 토큰에서 userEmail 가져오는 형식으로 변경해야됨
    @PostMapping("/products")
    public String orderProducts(@RequestBody List<OrderListRequestDto> orderList, @RequestParam String userEmail) {
        return orderService.orderProducts(orderList, userEmail);
    }
    
    // 주문 취소 /// 편집점 - 추후 결제 관련 로직 필요, userEmail 가져와서 본인주문인지 확인 필요
    @PostMapping("/cancellation")
    public String orderCancellation(@RequestParam Long orderId, @RequestParam String userEmail) {
        return orderService.OrderCancellation(orderId, userEmail);
    }

    // 반품신청 /// 편집점 - 추후 결제 관련 로직 필요, userEmail 가져와서 본인주문인지 확인 필요
    @PostMapping("/refund")
    public String orderRefund(@RequestParam Long orderId, @RequestParam String userEmail) {
        return orderService.orderRefund(orderId, userEmail);
    }
}
