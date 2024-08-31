import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Kotlin必須
    kotlin("jvm") version "2.0.10"

    // SpringBoot使用時に必須・推奨
    // 参考: <https://spring.pleiades.io/guides/tutorials/spring-boot-kotlin>
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.spring") version "2.0.20"
    kotlin("plugin.jpa") version "2.0.20"

    // Lambdaのために実行可能jarを作るGradleタスクを自動生成する
    // ユーザガイ: <https://gradleup.com/shadow/>
    id("com.gradleup.shadow") version "8.3.0"
}

group = "org.example"
version = "0.1"

java {
    toolchain {
        // Javaのバージョン指定はこうする2024年9月現在
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.AMAZON)
    }
}

repositories {
    mavenCentral()
}

val kotlinxSerializationVersion = "1.3.2"
val springCloudFunctionVersion = "4.1.3"
val awsJavaSdkVersion = "3.13.0"

dependencies {
    // SpringBootで必須
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Cloud FunctionをLambdaで使うのに必須
    implementation("org.springframework.cloud:spring-cloud-function-kotlin:$springCloudFunctionVersion")
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws:$springCloudFunctionVersion")

    // JSONの変換に必須
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    implementation("com.amazonaws:aws-lambda-java-events:$awsJavaSdkVersion")
}

tasks.shadowJar {
    // 実行可能Jarの末尾にawsと付与される
    archiveClassifier.set("aws")
}

tasks.assemble {
    // assembleタスクに実行可能Jar生成を追加
    dependsOn(tasks.shadowJar)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        // Java側のコードもKotlinでNull安全に扱えるようにする（Java側で対応されていれば）
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}