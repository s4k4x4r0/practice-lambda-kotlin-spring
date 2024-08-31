plugins {
    kotlin("jvm") version "2.0.10"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.gradleup.shadow") version "8.3.0"
    id("org.barfuin.gradle.taskinfo") version "2.2.0"
}

group = "org.example"
version = "0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.AMAZON)
    }
}

repositories {
    mavenCentral()
}

val kotlinxSerializationVersion = "1.3.2"
val springCloudFunctionVersion = "4.1.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-function-kotlin:$springCloudFunctionVersion")
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws:$springCloudFunctionVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}

tasks.shadowJar {
    archiveClassifier.set("aws")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}