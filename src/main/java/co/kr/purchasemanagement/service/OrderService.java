package co.kr.purchasemanagement.service;

import co.kr.purchasemanagement.dto.OrderListRequestDto;
import co.kr.purchasemanagement.dto.WishListResponseDto;
import co.kr.purchasemanagement.entity.*;
import co.kr.purchasemanagement.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderListRepository productOrderListRepository;

    // 장바구니 리스트 가져오기
    public List<WishListResponseDto> getWishList(String userEmail) {
        List<WishListEntity> list = wishListRepository.findByUserEmail(userEmail);
        return list.stream()
                .map(WishListResponseDto::new)
                .toList();
    }

    // 장바구니에 상품 추가
    public String addWishList(Long productId, String userEmail, int quantity) {
        if (wishListRepository.existsByProductIdAndUserEmail(productId, userEmail)){
            return "이미 장바구니에 추가한 상품입니다";
        }else {
            ProductEntity product = productRepository.findById(productId)
                    .orElse(null);
            if (product != null){
                wishListRepository.save(new WishListEntity(product, userEmail, quantity));
                return "장바구니에 해당 상품이 추가되었습니다.";
            }else {
                return "장바구니 추가 중 에러가 발생하였습니다.";
            }

        }
    }
    
    // 장바구니 수량 변경
    public String editQuantityWishList(Long wishListId, int quantity) {
        return wishListRepository.findById(wishListId)
                .map(wishList -> {
                    wishList.setQuantity(quantity);
                    wishListRepository.save(wishList);
                    return "수정 완료";
                })
                .orElse("해당 상품이 존재하지 않습니다.");
    }
    
    // 장바구니에서 상품 삭제
    public String deleteQuantityWishList(Long wishListId) {
        try {
            wishListRepository.deleteById(wishListId);
        } catch (Exception e) {
            return "삭제중 오류가 발생하였습니다.";
        }
        return "삭제가 완료되었습니다.";
    }

    // 상품 주문 -- 편집점 -- 결제 관련 수정 필요
    @Transactional
    public String orderProducts(List<OrderListRequestDto> orderListRequestDto, String userEmail) {
        try {
            // 재고 확인 및 감소
            for (OrderListRequestDto orderList : orderListRequestDto) {
                checkQuantityOrThrow(orderList);
            }

            // 주문서 저장
            ProductOrderEntity order = new ProductOrderEntity(userEmail);
            productOrderRepository.save(order);

            // 주문 리스트로 변환 및 저장
            List<ProductOrderListEntity> orderList = orderListRequestDto.stream()
                    .map(dto -> new ProductOrderListEntity(order.getOrderId(), dto))
                    .toList();
            productOrderListRepository.saveAll(orderList);

            return "주문이 완료되었습니다.";
        } catch (RuntimeException e) {
            return "주문 진행 중 오류: " + e.getMessage();
        }
    }


    // 재고 확인 및 감소 메소드
    public void checkQuantityOrThrow(OrderListRequestDto orderListRequestDto) {
        Optional<ProductEntity> productO = productRepository.findById(orderListRequestDto.getProductId());
        ProductEntity product = productO.orElseThrow(() ->
                new RuntimeException(orderListRequestDto.getProductName() + " 이 현재 존재하지 않습니다."));

        if (product.getProductQuantity() >= orderListRequestDto.getQuantity()) {
            product.setProductQuantity(product.getProductQuantity() - orderListRequestDto.getQuantity());
            productRepository.save(product);
        } else {
            throw new RuntimeException(orderListRequestDto.getProductName() + " 의 재고가 부족합니다.");
        }
    }
    
    
    // 주문 취소
    @Transactional
    public String OrderCancellation(Long orderId, String userEmail) {
        Optional<ProductOrderEntity> orderO = productOrderRepository.findById(orderId);
        ProductOrderEntity order;
        if (orderO.isPresent()) {
            order = orderO.get();
        }else {
            return "주문취소 오류 : 주문번호 오류";
        }
        
        // 주문서의 유저 이름 꺼내기
        String orderUser = order.getUserEmail().getEmail();
        
        // 주문한 유저와 신청 유저의 email 이 다르다면
        if (orderUser.equals(userEmail)) {
            return "주문취소 오류 : 잘못된 접근";
        }

        if (order.getOrderState() != OrderStateEnum.Order_Completed){
            return "주문취소 오류 : 이미 배송된 상품은 주문취소가 불가합니다.";
        }
        
        // 재고처리
        List<ProductOrderListEntity> orderLists = productOrderListRepository.findAllByOrderId(orderId);
        for (ProductOrderListEntity orderList : orderLists) {
            Optional<ProductEntity> productO = productRepository.findById(orderList.getProductId());
            ProductEntity product;
            if (productO.isPresent()) {
                product = productO.get();
                product.setProductQuantity(product.getProductQuantity() - orderList.getQuantity());
                productRepository.save(product);
            }
        }

        order.setOrderState(OrderStateEnum.Order_Cancellation);
        productOrderRepository.save(order);
        return "주문이 취소되었습니다.";
    }

    // 반품 신청
    public String orderRefund(Long orderId, String userEmail) {
        Optional<ProductOrderEntity> orderO = productOrderRepository.findById(orderId);
        ProductOrderEntity order;
        if (orderO.isPresent()) {
            order = orderO.get();
        }else {
            return "반품신청 오류 : 주문 번호 오류";
        }

        // 주문서의 유저 이름 꺼내기
        String orderUser = order.getUserEmail().getEmail();

        // 주문한 유저와 신청 유저의 email 이 다르다면
        if (orderUser.equals(userEmail)) {
            return "반품신청 오류 : 잘못된 접근";
        }
        
        // 상품을 받은지 1일 이상이 지났다면

        if (order.getOrderState() != OrderStateEnum.Delivery_Completed){
            return "반품신청 오류 : 배송완료 되지 않은 상품은 반품이 불가능합니다.";
        }
        
        // 시간계산
        LocalDateTime limitTime = order.getUpdateAt()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .plusDays(1);   // LocalDateTime 으로 변환 및 배송받은 날짜로부터 1일 추가
        if (limitTime.isAfter(LocalDateTime.now())) {
            return "반품신청 오류 : 배송완료 후 만 1일이 지난 상품은 환불이 불가능합니다.";
        }

        // 주문의 Status 변환
        order.setOrderState(OrderStateEnum.Refunding);
        order.setRefundAt(new Date());
        productOrderRepository.save(order);
        return "반품신청이 완료되었습니다.";
    }
}
