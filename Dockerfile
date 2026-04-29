FROM eclipse-temurin:11-jdk-jammy AS builder
WORKDIR /workspace
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true
COPY . .
RUN ./gradlew clean bootWar --no-daemon

FROM eclipse-temurin:11-jre-jammy AS runtime
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.war app.war
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --spider -q http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java","-jar","/app/app.war","--spring.profiles.active=deploy"]
