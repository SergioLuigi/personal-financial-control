import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	kotlin("plugin.jpa") version "1.6.10"
	jacoco
}

group = "com.sergioluigi"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2021.0.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("io.github.openfeign.form:feign-form")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springframework.cloud:spring-cloud-openfeign-core")
	runtimeOnly("org.postgresql:postgresql")
	implementation("com.amazonaws:aws-java-sdk:1.12.196")
	implementation("com.amazonaws:aws-java-sdk-core:1.12.196")
	implementation("org.json:json:20220320")
	//SECURITY
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.security:spring-security-oauth2-client")
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("com.amazonaws:aws-java-sdk-cognitoidentity:1.12.196")

	//TEST
	testImplementation(kotlin("test-common"))
	testImplementation(kotlin("test-annotations-common"))
	testImplementation("io.mockk:mockk-common")
	testImplementation("org.springframework.boot:spring-boot-starter-test"){
		exclude(module = "mockito-core")
		exclude(module = "junit-vintage-engine")
	}
	testImplementation("com.ninja-squad:springmockk")
	testImplementation("org.springframework.security:spring-security-test")
	//testImplementation("com.h2database:h2:2.1.210")

}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
