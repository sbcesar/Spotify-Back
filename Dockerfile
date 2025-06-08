FROM gradle:8.5-jdk17 AS build
COPY . /home/app
WORKDIR /home/app

RUN chmod +x ./gradlew

RUN ./gradlew build

FROM azul/zulu-openjdk:17-latest
COPY --from=build /home/app/build/libs/*.war app.war
CMD ["java", "-jar", "/app.war"]