# Purchase Management
<br>

## 프로젝트 소개

> 프로젝트 목적

이 프로젝트는 마이크로서비스 아키텍처(Microservices Architecture)를 기반으로 설계되었습니다.  
상품에 대한 정보의 정합성을 추구하며,  
대규모 요청을 처리하는 것을 목표로 하는 프로젝트 입니다.

<br>

> 개발환경

- 프로그래밍 언어 : **JAVA 21**
- 빌드 도구 : **gradle 8.11.1**
- 프레임워크 : **SpringBoot 3.2.0**
- 보안 : **Security 6.1.0, JJWT 0.11.5**
- 캐싱 : **Redis**
- 메세지 발행 : **Kafka 3.5.1**
- 데이터베이스 : **MySql 8.0**
- ORM : **Spring Data JPA 3.1.0**
- SpringCloud 2023.0.0
  - Client : **Netflix-Eureka**
  - Gateway : **WebFlux 방식**

- 자동화 도구 : **Docker, Docker-compose**
- Api-test : PostMan, K6, python

<br>

## **프로젝트 기능**
> 프로젝트 구조

![Project_Structure](https://github.com/user-attachments/assets/b1c6b5a2-46d0-46d5-a693-5d38a571312e)

<br>

> 각 서비스 주요 기능

**api-gateway** 
- API 요청 라우팅 및 JWT 토큰 검증

- 들어온 각 요청들을 비동기적으로 각 서비스에 요청

**user-service** : 
- 사용자 인증 및 JWT 생성 / 반환
- 사용자 로그인 정보 캐시 저장

**product-service**
- 상품 최초 색인시 내부 서비스에서 필요한 정보 캐시 저장
- 상품이 이미 캐시에 저장되어 있을시, 캐시에서 갯수 반환

**order-service** : 주문 생성 및 관리
- 상품 주문시 캐시에서 상품 리스트의 각 상품 갯수 확인 및 감소
- 상품 갯수 확인중 특정 상품의 갯수가 부족할 경우, <br>
  해당 상품 리스트에서 이미 감소시킨 상품들의 갯수 복구.

<br>

## 성능개선

## 트러블슈팅


## API 요청
#### 유저 - 로그인 관련
| 용도               | Mapping        | API path                         | 인자                                                                     |
|--------------------|----------------|----------------------------------|--------------------------------------------------------------------------|
| 로그인             | `@PostMapping` | `/api/user/security/login`        | `@RequestBody` Map<String, String> loginData, HttpServletRequest request |
| 로그아웃           | `@PostMapping` | `/api/user/security/logout`       | `@RequestHeader` Authorization, HttpServletRequest request               |
| 모든 곳에서 로그아웃 | `@PostMapping` | `/api/user/security/logoutAll`    | `@RequestHeader` Authorization                                          |

---

#### 유저 - 회원가입 관련
| 용도          | Mapping       | API path                           | 인자                                                                  |
| ------------ | ---------------| ---------------------------------- | --------------------------------------------------------------------- |
| 회원 가입     | `@PostMapping` | `/api/user/common/register`        | `@RequestBody` UserEntity(userName, email, password)                  |
| 이메일 인증   | `@GetMapping`  | `/api/user/common/verify`          | `@RequestParam` code, `@RequestParam` email                           |
| 비밀번호 변경 | `@PostMapping` | `/api/user/common/change-password` | `@RequestHeader` Authorization(bearerToken), `@RequestParam` password |

---

#### 상품 관련
| 용도           | Mapping        | API path                             | 인자                                       |
|----------------|----------------|--------------------------------------|--------------------------------------------|
| 상품 리스트 조회 | `@GetMapping`  | `/api/product`                      | `@RequestParam` size, `@RequestParam` page |
| 상품 상세 조회   | `@GetMapping`  | `/api/product/{productId}`          | `@PathVariable` productId                  |
| 상품 수량 조회   | `@GetMapping`  | `/api/product/quantity/{productId}` | `@PathVariable` productId                  |
| 상품 가격 조회   | `@GetMapping`  | `/api/product/price/{productId}`    | `@PathVariable` productId                  |

---

#### 주문 및 위시리스트 관련 API </br>
| 용도                   | Mapping       | API path                                  | 인자                                                                                                             |
|------------------------|---------------|-------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| 위시리스트 조회         | `@GetMapping` | `/api/order/wishList`                     | `@RequestHeader` Authorization                                                                                   |
| 위시리스트에 상품 추가  | `@PostMapping` | `/api/order/wishList/{productId}`         | `@PathVariable` productId, `@RequestParam` quantity, `@RequestParam` productName, `@RequestHeader` Authorization |
| 위시리스트 수량 수정    | `@GetMapping`  | `/api/order/wishList/edit/quantity`       | `@RequestParam` wishListId, `@RequestParam` quantity, `@RequestHeader` Authorization                             |
| 위시리스트 삭제         | `@GetMapping`  | `/api/order/wishList/delete/{wishListId}` | `@PathVariable` wishListId, `@RequestHeader` Authorization                                                       |
| 상품 주문              | `@PostMapping` | `/api/order/products`                     | `@RequestBody` List<OrderItemRequestDto>, `@RequestHeader` Authorization                                         |
| 주문 결제 완료         | `@PostMapping`  | `/api/order/products/payed/{orderId}`     | `@PathVariable` orderId, `@RequestHeader` Authorization                                                         |
| 주문 취소              | `@PostMapping` | `/api/order/cancellation`                 | `@RequestParam` orderId, `@RequestHeader` Authorization                                                          |
| 반품 신청              | `@PostMapping` | `/api/order/refund`                       | `@RequestParam` orderId, `@RequestHeader` Authorization                                                          |
| 주문 정보 목록 조회     | `@GetMapping`  | `/api/order/show`                         | `@RequestHeader` Authorization                                                                                   |
| 특정 주문 정보 조회     | `@GetMapping`  | `/api/order/show/{orderId}`               | `@PathVariable` orderId, `@RequestHeader` Authorization                                                          |
