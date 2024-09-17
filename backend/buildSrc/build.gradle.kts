plugins {
    // Kotlin必須
    kotlin("jvm") version "2.0.10"
}

group = "com.example"

repositories {
    mavenCentral()
}

val awsKotlinSdkVersion = "1.3.32"

dependencies {
    // AWS SDK for Kotlin
    implementation("aws.sdk.kotlin:s3:$awsKotlinSdkVersion")
    implementation("aws.sdk.kotlin:lambda:$awsKotlinSdkVersion")
}