plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	war
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlin.kapt") version "1.9.25"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
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

	// --- Spring Boot starters (Web, Seguridad, Validación, MongoDB) ---
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	// --- Firebase & Stripe ---
	implementation("com.google.firebase:firebase-admin:9.2.0")
	implementation("com.stripe:stripe-java:29.2.0")

	// --- Kotlin & Jackson ---
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// --- MapStruct para mapeo de DTOs ---
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

	// --- Spring Boot devtools ---
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// --- Procesador de configuración de Spring Boot ---
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// --- Tomcat (servidor embebido en tiempo de ejecución) ---
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

	// --- Testing (JUnit, Spring Boot Test, Mockito, Seguridad) ---
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")

	// --- Soporte para Mockito en Kotlin ---
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
	testImplementation("org.mockito:mockito-inline:5.2.0")

	// --- Permite ejecutar tests con JUnit Platform (JUnit 5) ---
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
