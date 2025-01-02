package co.kr.orderservice.order.service;

import co.kr.orderservice.feign.toProduct.ProductClient;
import co.kr.orderservice.feign.toProduct.ProductResponseDto;
import co.kr.orderservice.kafka.toProduct.ProductProducer;
import co.kr.orderservice.order.entity.*;

import co.kr.orderservice.order.repository.ProductOrderListRepository;
import co.kr.orderservice.order.repository.ProductOrderRepository;
import co.kr.orderservice.order.repository.WishListRepository;
import co.kr.orderservice.order.util.JwtTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    private final WishListRepository wishListRepository;
    private final ProductClient productClient;
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderListRepository productOrderListRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ProductProducer productProducer;

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
            ProductResponseDto productResponseDto = productClient.getProduct(productId);
            if (productResponseDto.getProductName() != null){
                wishListRepository.save(new WishListEntity(userEmail, productResponseDto, quantity));
                return "장바구니에 해당 상품이 추가되었습니다.";
            }else {
                return "해당 상품이 존재하지 않습니다..";
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
            // 존재 확인
            if(wishListRepository.existsById(wishListId)) {
                return "위시리스트에 해당 상품이 없습니다.";
            }
            // 삭제
            wishListRepository.deleteById(wishListId);
        } catch (Exception e) {
            return "삭제중 오류가 발생하였습니다.";
        }
        return "삭제가 완료되었습니다.";
    }

    // 상품 주문서 만들기
    public String orderProducts(List<OrderItemRequestDto> orderListRequestDto, String bearerToken) {
        try {
            String token = bearerToken.substring(7); // 토큰 정제
            String userEmail = jwtTokenUtil.getUserEmailFromToken(token); // 토큰에서 이메일 꺼내기

            ProductOrderEntity order = new ProductOrderEntity(userEmail); // 꺼낸 이메일로 주문서 만들기
            productOrderRepository.save(order); // 주문서 저장

            // 재고처리 및 확인 결과 반환
            String result = productProducer.decreaseQuantity(order.getOrderId(), orderListRequestDto);
            System.out.println("kafka 메세지 reply 결과" + result);
            if (!result.equals("null")){
                return result;
            }else {
                // 주문 리스트로 변환 및 저장
                List<ProductOrderItemEntity> orderList = orderListRequestDto
                        .stream().map(dto -> new ProductOrderItemEntity(order.getOrderId(), dto))
                        .toList();
                System.out.println("주문 갯수 : " + orderList.size());

                productOrderListRepository.saveAll(orderList);

                return "주문 완료";
            }
        }catch (Exception e) {
            System.out.println("주문 처리중 에러 : " + e.getMessage());
            return "주문 처리중 에러 발생 : " + e.getMessage();
        }
    }

    // 주문 취소
    @Transactional
    public String OrderCancellation(Long orderId, String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            String userEmail = jwtTokenUtil.getUserEmailFromToken(token);
            Optional<ProductOrderEntity> orderO = productOrderRepository.findById(orderId);
            ProductOrderEntity order;
            if (orderO.isPresent()) {
                order = orderO.get();
            }else {
                return "주문취소 오류 : 주문번호 오류";
            }

            // 주문서의 유저 이름 꺼내기
            String orderUser = order.getUserEmail();

            // 주문한 유저와 신청 유저의 email 이 다르다면
            if (orderUser.equals(userEmail)) {
                return "주문취소 오류 : 잘못된 접근";
            }

            if (order.getOrderState() != OrderStateEnum.Order_Completed){
                return "주문취소 오류 : 이미 배송된 상품은 주문취소가 불가합니다.";
            }

            List<OrderItemRequestDto> orderItemList = productOrderListRepository.findAllByOrderId(orderId)
                    .stream().map(entity -> new OrderItemRequestDto(entity))
                    .toList();

            // 재고처리 요청
            productProducer.increaseQuantity(orderId, orderItemList);

            order.setOrderState(OrderStateEnum.Order_Cancellation);
            productOrderRepository.save(order);
            return "주문이 취소되었습니다.";
        } catch (Exception e) {
            return "주문 취소 오류 : " + e.getMessage();
        }
    }

    // 반품 신청
    public String orderRefund(Long orderId, String bearerToken) {
        String token = bearerToken.substring(7);
        String userEmail = jwtTokenUtil.getUserEmailFromToken(token);
        Optional<ProductOrderEntity> orderO = productOrderRepository.findById(orderId);
        ProductOrderEntity order;
        if (orderO.isPresent()) {
            order = orderO.get();
        }else {
            return "반품신청 오류 : 주문 번호 오류";
        }

        // 토큰의 유저 이름 꺼내기
        String orderUser = jwtTokenUtil.getUserEmailFromToken(token);

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
