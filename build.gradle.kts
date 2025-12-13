plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.hotel"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Веб-сервер та шаблонізатор (Thymeleaf)
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // --- ВАЖЛИВО: Бібліотека для Layouts (щоб працювало layout:decorate у HTML) ---
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")

    // Робота з Базою Даних (JDBC)
    // У starter-jdbc є JdbcTemplate, який ми використовуємо в репозиторіях
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // Драйвер PostgreSQL
    runtimeOnly("org.postgresql:postgresql")

    // Інструменти розробника (Lombok, гаряче перезавантаження)
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")

    // Тестування
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}