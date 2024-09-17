import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.S3Exception
import aws.smithy.kotlin.runtime.content.asByteStream
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.internal.file.copy.CopyActionProcessingStream
import org.gradle.api.internal.file.CopyActionProcessingStreamAction
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.WorkResult
import org.gradle.workers.internal.DefaultWorkResult
import java.net.URI


abstract class S3CopyTask : AbstractCopyTask() {

    private var bucketName: String? = null
    private var prefix: String = ""

    fun into(destinationUri: String) {
        val uri = URI.create(destinationUri)
        if (uri.scheme != "s3") {
            throw IllegalArgumentException("Destination must be in the format s3://bucket-name/prefix/")
        }
        this.bucketName = uri.host
        this.prefix = uri.path.trimStart('/').trimEnd('/')
    }

    override fun createCopyAction(): CopyAction {
        return object: CopyAction {
            override fun execute(stream: CopyActionProcessingStream): WorkResult {
                runBlocking {
                    val s3Client = S3Client.fromEnvironment()
                    val jobs = mutableListOf<Job>()
                    stream.process(object: CopyActionProcessingStreamAction {
                        override fun processFile(details: FileCopyDetailsInternal) {
                            if (!details.isDirectory) {
                                val relativePath = details.relativePath.toString()
                                val s3Key = if (prefix.isEmpty()) relativePath else "$prefix/$relativePath"
                                val file = details.file
                                val job = launch {
                                    try {
                                        val putObjectRequest = PutObjectRequest {
                                            bucket = bucketName
                                            key = s3Key
                                            body = file.asByteStream()
                                        }
                                        s3Client.putObject(putObjectRequest)
                                        logger.lifecycle("Uploaded ${file.path} to s3://$bucketName/$s3Key")
                                    } catch (e: S3Exception) {
                                        logger.error("Failed to upload ${file.path} to s3://$bucketName/$s3Key: ${e.message}")
                                    }
                                }
                                jobs.add(job)
                            }
                        }
                    })
                    jobs.joinAll()
                    s3Client.close()
                }
                return DefaultWorkResult.SUCCESS
            }
        }
    }
}