FROM gradle:8.4.0-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon

FROM azul/zulu-openjdk:17
COPY --from=builder /app/build/libs/*.war /app/app.war
CMD ["java", "-jar", "/app/app.war"]

