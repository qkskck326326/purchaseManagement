package co.kr.productservice.product.service;


import co.kr.productservice.product.entity.ProductEntity;
import co.kr.productservice.product.entity.ProductResponseDto;
import co.kr.productservice.product.repository.ProductRepository;
import co.kr.productservice.redis.RedisUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductService {
    private ProductRepository productRepository;
    private RedisUtil redisUtil;

    public List<ProductResponseDto> getList(int size, int page) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ProductEntity> list = productRepository.findAll(pageRequest).getContent();

        return list.stream()
                .map(ProductResponseDto::new)
                .toList();
    }

    public ProductEntity getProduct(Long productId) {
        return productRepository.findById(productId).orElseGet(ProductEntity::new);
    }


    // 상품 갯수 가져오기
    // 원래 상품을 등록하면서 Redis에 같이 등록되는것이 정상 시퀀스이지만,
    // 테스트를 위해 아래와 같이 만듬
    public int getProductQuantity(Long productId) {
        // 레디스에서 가져오기
        Integer quantity = redisUtil.getProductQuantity(productId);
        if (quantity == null){ // 레디스에 저장되어 있지 않다면 DB 에서 가져옴
            Optional<ProductEntity> productO = productRepository.findById(productId);
            if (productO.isPresent()){ // DB에 존재한다면 레디스에 해당값을 저장하고 리턴함
                ProductEntity product = productO.get();
                redisUtil.saveProductQuantity(product.getProductId(), product.getProductQuantity());
                return product.getProductQuantity();
            }else { // 둘 다 에서 없는 경우에는 없는 상품이기 때문에 에러숫자로 -1 을 리턴함
                return -1;
            }
        }else { // 레디스에 저장되어 있다면 바로 리턴
            return quantity;
        }
    }
}
