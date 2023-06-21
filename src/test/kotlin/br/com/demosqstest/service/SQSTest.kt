package br.com.demosqstest.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.*

@SpringBootTest
@Testcontainers
class SQSTest {

    @Autowired
    private lateinit var sqsClient: SqsClient

    @Value("\${aws.sqs.queue-name}")
    private lateinit var queueName: String

    @Test
    fun testSendMessageToQueue() {

        val messageBody = "Hello, SQS!"

        val createQueueRequest = sqsClient.createQueue { it.queueName(queueName) }
        val queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl()

        val sendMessageRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(messageBody)
            .build()

        val sendMessageResponse: SendMessageResponse = sqsClient.sendMessage(sendMessageRequest)
        val messageId = sendMessageResponse.messageId()

        println("Mensagem enviada com sucesso. MessageId: $messageId")

        // Verifica se a mensagem foi enviada com sucesso
        assertNotNull(messageId)

        // Aguarda um tempo para permitir que a mensagem seja processada
        Thread.sleep(2000)

        // Obtém as mensagens presentes na fila
        val receiveResult = sqsClient.receiveMessage(
            ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .build())

        // Verifica se a mensagem enviada está presente na fila
        Assertions.assertEquals(1, receiveResult.messages().size)
        val receivedMessage = receiveResult.messages()[0]
        Assertions.assertEquals(messageBody, receivedMessage.body())

        // Exclui a mensagem da fila
        sqsClient.deleteMessage(
            DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receivedMessage.receiptHandle())
                .build())
    }
}