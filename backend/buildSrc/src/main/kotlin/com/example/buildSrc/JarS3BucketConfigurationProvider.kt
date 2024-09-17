package com.example

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.getBucketTagging
import aws.sdk.kotlin.services.s3.model.S3Exception
import com.example.buildSrc.buildConfig.BuildConfig
import kotlinx.coroutines.runBlocking
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class JarS3BucketConfigurationProvider @Inject constructor(
    private var providerFactory: ProviderFactory
) {
    val bucketName = providerFactory.provider<String> {
        runBlocking {
            S3Client.fromEnvironment().use { s3 ->
                s3
                    .listBuckets()
                    .buckets?.find { bucket ->
                        val bucketName = bucket.name ?: return@find false
                        val taggingResponse = try {
                            s3.getBucketTagging { this.bucket = bucketName }
                        } catch (e: S3Exception) {
                            return@find false
                        }
                        val tags = taggingResponse.tagSet.associate { it.key to it.value }
                        return@find isMatchTagCondtion(tags)
                    }?.name ?: throw RuntimeException("Jarバケットが見つかりませんでした")
            }
        }
    }

    private fun isMatchTagCondtion(tags: Map<String, String>) =
        tags["Project"] == BuildConfig.APPLICATION_NAME && tags["Role"] == "backend-jar"
}