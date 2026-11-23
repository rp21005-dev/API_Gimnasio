# Etapa de construcción
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew bootJar -x test

# Etapa de ejecución
FROM eclipse-temurin:17-jre

WORKDIR /app

# Zona horaria del gimnasio: "hoy" debe ser el mismo día que ven los usuarios,
# aunque la plataforma corra en UTC (Railway, kind, GitHub Actions)
ENV TZ=America/El_Salvador

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]