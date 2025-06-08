FROM gradle:8.4.0-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon

FROM azul/zulu-openjdk:17
COPY --from=builder /app/build/libs/*.war /app/app.war

# Copiar el script de entrada
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Ejecutar el script al arrancar
CMD ["/entrypoint.sh"]
