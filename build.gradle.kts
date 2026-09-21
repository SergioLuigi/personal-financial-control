import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
	java
	id("org.springframework.boot") version "4.1.1"
}

group = "br.com.sergioluigi"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

val springModulithVersion = "2.1.1"

dependencies {
	// Dependency versions come from Gradle's native BOM support rather than the
	// io.spring.dependency-management plugin, which Spring keeps only for backwards
	// compatibility. Both BOMs have to reach the test classpath as well.
	implementation(platform(SpringBootPlugin.BOM_COORDINATES))
	implementation(platform("org.springframework.modulith:spring-modulith-bom:$springModulithVersion"))
	annotationProcessor(platform(SpringBootPlugin.BOM_COORDINATES))
	testImplementation(platform(SpringBootPlugin.BOM_COORDINATES))
	testImplementation(platform("org.springframework.modulith:spring-modulith-bom:$springModulithVersion"))
	testAnnotationProcessor(platform(SpringBootPlugin.BOM_COORDINATES))

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-liquibase")
	implementation("org.springframework.modulith:spring-modulith-starter-core")
	implementation("org.springframework.modulith:spring-modulith-starter-jpa")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
	runtimeOnly("org.springframework.modulith:spring-modulith-observability-core")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.modulith:spring-modulith-starter-test")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-mysql")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
