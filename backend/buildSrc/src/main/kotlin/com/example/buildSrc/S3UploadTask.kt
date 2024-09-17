package com.example.buildSrc

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest.Companion.invoke
import aws.smithy.kotlin.runtime.content.asByteStream
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class S3UploadTask : DefaultTask() {

    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:Input
    abstract val bucketName: Property<String>

    @get:Input
    @get:Optional
    abstract val prefix: Property<String>

    @TaskAction
    fun uploadFile() {
        val file = inputFile.get().asFile
        val prefixTrimmed =
            if (prefix.isPresent) prefix.get().trimStart('/').trimEnd('/')
            else null

        val bucket = bucketName.get()
        val key = if (prefixTrimmed.orEmpty().isEmpty()) file.name
        else "${prefixTrimmed}/${file.name}"

        runBlocking {
            S3Client.fromEnvironment().use { s3 ->
                val putRequest = PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = file.asByteStream()
                }
                s3.putObject(putRequest)
                logger.lifecycle("File uploading to S3: s3://$bucket/$key")
            }
        }
        logger.lifecycle("File uploaded to S3: s3://$bucket/$key")
    }
}