package co.kr.purchasemanagement.service;

import co.kr.purchasemanagement.dto.OrderListRequestDto;
import co.kr.purchasemanagement.dto.WishListResponseDto;
import co.kr.purchasemanagement.entity.ProductEntity;
import co.kr.purchasemanagement.entity.ProductOrderEntity;
import co.kr.purchasemanagement.entity.ProductOrderListEntity;
import co.kr.purchasemanagement.entity.WishListEntity;
import co.kr.purchasemanagement.repository.ProductOrderListRepository;
import co.kr.purchasemanagement.repository.ProductOrderRepository;
import co.kr.purchasemanagement.repository.ProductRepository;
import co.kr.purchasemanagement.repository.WishListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
}
