# Purchase Management
<br>

## 프로젝트 소개

> 프로젝트 목적

이 프로젝트는 마이크로서비스 아키텍처(Microservices Architecture)를 기반으로 설계되었습니다.  
상품에 대한 정보의 정합성을 추구하며,  
대규모 요청을 처리하는 것을 목표로 하는 프로젝트 입니다.

<br>

> 사용기술
>- 프로그래밍 언어 : **JAVA 21**
>- 빌드 도구 : **gradle 8.11.1**
>- 프레임워크 : **SpringBoot 3.2.0**
>- 보안 : **Security 6.1.0, JJWT 0.11.5**
>- 데이터베이스 : **MySql 8.0**
>- ORM : **Spring Data JPA 3.1.0**
>
>> 분산 시스템 및 서비스 관리
>>- 서비스 관리: **Spring Cloud 2023.0.0 (Spring Cloud Gateway 포함)**
>>- 캐싱 : **Redis**
>>- 메세지 발행 : **Kafka 3.5.1**
>
>> 배포 및 운영
>>- CI/CD : **Docker, Docker-compose**
>
>> 테스트 및 모니터링  
>>- 모니터링 : Prometheus, Grafana
>>- Api-test : PostMan, K6, python

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
- 상품 갯수 확인중 특정 상품의 갯수가 부족할 경우, 
  해당 상품 리스트에서 이미 감소시킨 상품들의 갯수 복구.

<br>

## 성능개선

## 트러블슈팅


## Post Man API 문서 링크
https://documenter.getpostman.com/view/38023455/2sAYX2Pjpc
