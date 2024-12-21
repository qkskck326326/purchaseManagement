# 빌드 단계 (Gradle 빌드 포함)
FROM gradle:8.11.1-jdk21 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 파일 복사 (예: Gradle 빌드 파일, 소스 코드)
COPY . .

# Gradle 빌드 실행 (JAR 파일을 build/libs에 생성)
RUN gradle clean build

# 실행 단계 (JDK 21 기반)
FROM openjdk:21-jdk-slim

# 빌드한 JAR 파일을 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
