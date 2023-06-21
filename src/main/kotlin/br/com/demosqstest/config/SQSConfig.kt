package br.com.demosqstest.config

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class SQSConfig (

    @Value("\${aws.access-key}")
    private val accessKeyId: String,

    @Value("\${aws.secret-key}")
    private val secretKey: String,

    @Value("\${aws.sqs.endpoint}")
    private val sqsEndpoint: String,

    @Value("\${aws.region.static}")
    private val awsRegion: String

) {
    @Bean
    fun sqsClient(): SqsClient {
        return SqsClient.builder()
            .endpointOverride(URI(sqsEndpoint))
            .region(Region.of(awsRegion))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretKey)))
            .build()
    }

    @Bean
    fun amazonSQS(sqsClient: SqsClient): AmazonSQS {
        return AmazonSQSClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(sqsEndpoint, awsRegion))
            .build()
    }
}