# Etapa de construcción
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew bootJar -x test

# Etapa de ejecución
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]