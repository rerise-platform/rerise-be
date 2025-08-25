# ---- Build stage (JDK 17) ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Gradle 캐시 최적화: 먼저 래퍼/설정만 복사
COPY gradlew settings.gradle* build.gradle* ./
COPY gradle/ gradle/
RUN chmod +x gradlew

# 의존성 프리캐시(소스 없이 메타만으로 캐시 생성)
RUN ./gradlew --no-daemon dependencies || true

# 실제 소스 복사 후 빌드 (테스트는 해커톤 기준으로 스킵 권장)
COPY . .
RUN chmod +x gradlew && ./gradlew --no-daemon clean bootJar -x test

# ---- Run stage (JRE 17) ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# 보안상 비루트 사용자
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# 빌드 산출물 반입
COPY --from=build /workspace/build/libs/*.jar /app/app.jar

# 런타임 기본값
ENV TZ=Asia/Seoul \
    SERVER_FORWARD_HEADERS_STRATEGY=framework \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=prod"]
