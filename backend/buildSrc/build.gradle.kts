plugins {
    // Kotlin必須
    kotlin("jvm") version "2.0.10"

    // 定数
    id("com.github.gmazzo.buildconfig") version "5.4.0"
}

group = "com.example.buildSrc"

repositories {
    mavenCentral()
}

val awsKotlinSdkVersion = "1.3.32"

dependencies {
    // AWS SDK for Kotlin
    implementation("aws.sdk.kotlin:s3:$awsKotlinSdkVersion")
    implementation("aws.sdk.kotlin:lambda:$awsKotlinSdkVersion")
}

val applicationName = "practice-lambda-kotlin-spring"

buildConfig {
    packageName = "${group}.buildConfig"
    buildConfigField("APPLICATION_NAME", applicationName)
}