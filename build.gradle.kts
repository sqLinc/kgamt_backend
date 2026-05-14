plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"

	id("org.springframework.boot") version "3.5.10"
	id("io.spring.dependency-management") version "1.1.7"
	id("application")
}

springBoot {
	mainClass.set("com.kgamt.menu.MenuApplicationKt")
}

tasks.bootJar {
	mainClass.set("com.kgamt.menu.MenuApplicationKt")
	launchScript()           // делает JAR исполняемым
	enabled = true
}

tasks.jar {
	enabled = false
}


tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
	mainClass.set("com.kgamt.menu.MenuApplicationKt")
	launchScript()
}



group = "com.kgamt"
version = "0.0.1-SNAPSHOT"
description = "Backend for college canteen project"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("org.apache.poi:poi-ooxml:5.2.5")
}

application {
	mainClass.set("com.kgamt.menu.MenuApplicationKt")
}





kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}