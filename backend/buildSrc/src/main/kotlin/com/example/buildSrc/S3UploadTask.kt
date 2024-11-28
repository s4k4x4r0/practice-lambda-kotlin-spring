package com.example.buildSrc

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest.Companion.invoke
import aws.sdk.kotlin.services.s3.model.Tag
import aws.smithy.kotlin.runtime.content.asByteStream
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
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

    @get:Input
    @get:Optional
    abstract val targetFileName: Property<String>

    @get:Input
    @get:Optional
    abstract val tags: MapProperty<String, String>

    @TaskAction
    fun uploadFile() {
        val file = inputFile.get().asFile
        val prefixTrimmed =
            if (prefix.isPresent) prefix.get().trimStart('/').trimEnd('/')
            else null

        val fileName = if (targetFileName.isPresent) targetFileName.get() else file.name
        
        val bucket = bucketName.get()
        val key = if (prefixTrimmed.orEmpty().isEmpty()) fileName
        else "${prefixTrimmed}/${fileName}"

        runBlocking {
            S3Client.fromEnvironment().use { s3 ->
                val putRequest = PutObjectRequest {
                    this.bucket = bucket
                    this.key = key
                    body = file.asByteStream()
                    if (tags.isPresent) {
                        tagging = tags.get().map { (key, value) -> Tag { 
                            this.key = key
                            this.value = value
                        }}.joinToString("&", transform = { "${it.key}=${it.value}" })
                    }
                }
                s3.putObject(putRequest)
                logger.lifecycle("File uploading to S3: s3://$bucket/$key")
            }
        }
        logger.lifecycle("File uploaded to S3: s3://$bucket/$key")
    }
}