# 1단계: 빌드 스테이지 (Gradle 빌드)
FROM gradle:7.6-jdk17 AS build

WORKDIR /home/gradle/project

COPY --chown=gradle:gradle . .

RUN gradle build --no-daemon -x test

# 2단계: 실행 스테이지
FROM eclipse-temurin:17-jdk

WORKDIR /app

# 1단계에서 빌드된 jar를 복사
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
