import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
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

    // JSONの処理（Kotlinx Serializer）に必要
    kotlin("plugin.serialization") version "2.0.20"

    // Gitバージョンを取得
    id("com.palantir.git-version") version "3.1.0"
    id("com.github.gmazzo.buildconfig") version "5.4.0"
}

group = "com.example"

// Gitからバージョンを自動で取得
val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion(mapOf("prefix" to "v-"))

buildConfig {
    buildConfigField("APP_VERSION", project.version.toString())
}

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

    // JSONの処理（Kotlinx Serializer）に必要
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    // API Gatewayとのインタフェースに必要
    implementation("com.amazonaws:aws-lambda-java-events:$awsJavaSdkVersion")

    // AOPで必須（共通でHTTPヘッダを設定するのに使用）
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // テストに必要
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.shadowJar {
    // 実行可能Jarの末尾にawsと付与される
    archiveClassifier.set("aws")

    // Springに必要なファイルをshadowJarにマージするように指示
    // Springに必須
    mergeServiceFiles()
    append("META-INF/spring.handlers")
    append("META-INF/spring.schemas")
    append("META-INF/spring.tooling")
    append("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")
    append("META-INF/spring/org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration.imports")
    transform(PropertiesFileTransformer().apply {
        paths = listOf("META-INF/spring.factories")
        mergeStrategy = "append"
    })
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

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        // META-INF/MANIFEST.MFファイルに、Main-Classエントリを設定
        // メインクラスを見つけるために必須
        // Lambdaに環境変数MAIN_CLASSを指定してもよい
        attributes["Main-Class"] = "com.example.ApplicationKt"
    }
}