FROM gradle:8.4.0-jdk17 AS builder
COPY --no-cache . /app
WORKDIR /app
RUN gradle build --no-daemon

FROM azul/zulu-openjdk:17
COPY --from=builder /app/build/libs/*.war /app/app.war
CMD ["java", "-jar", "/app/app.war"]
