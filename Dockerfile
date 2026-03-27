# 1. JDK 21 기반의 경량 이미지 사용 (eclipse-temurin 권장)
FROM eclipse-temurin:21-jdk-jammy

# 2. 작업 디렉토리 설정 (선택 사항이지만 권장)
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너로 복사
# Gradle 빌드 시 보통 build/libs/프로젝트명-버전-SNAPSHOT.jar로 생성됩니다.
ARG JAR_FILE=build/libs/cozystay-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 4. 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]