plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "gimnnasio"
version = "1.0.0"

//java {
//    toolchain {
//        languageVersion = JavaLanguageVersion.of(21)
//    }
//}

repositories {
    mavenCentral()
}

dependencies {
    // API REST
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Persistencia con JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Caché en memoria
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // Validación de datos
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Driver de PostgreSQL
    runtimeOnly("org.postgresql:postgresql")

    // Pruebas automatizadas
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    //Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Seguridad: Spring Security (incluye BCrypt para contraseñas)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Librería JJWT para generar y validar tokens
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

}

tasks.test {
    useJUnitPlatform()
}