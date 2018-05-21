package rabbitmq

import com.rabbitmq.client.*
import java.nio.charset.Charset.defaultCharset

private val TASK_QUEUE_NAME = "task_queue"

fun main(argv: Array<String>) {
    val factory = ConnectionFactory()
    factory.host = "localhost"
    val connection = factory.newConnection()
    val channel = connection.createChannel()

    channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null)
    println(" [*] Waiting for messages. To exit press CTRL+C")

    channel.basicQos(1)


    val consumer = object : DefaultConsumer(channel) {
        override fun handleDelivery(consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?, body: ByteArray?) {
            val message = String(body!!, defaultCharset())
            println(" [x] Received '$message'")
            try {
                doWork(message)
            } finally {
                println(" [x] Done") //тут получаем сообщение, но не отправляем Ack, сообщение переходит на другой консьюмер
                //                    channel.basicAck(envelope.getDeliveryTag(), false);
                //                    channel.abort();
                //                    channel.
            }
        }
    }

    val autoAck = false
    channel.basicConsume(TASK_QUEUE_NAME, autoAck, consumer)
}

private fun doWork(task: String) {
    for (ch in task.toCharArray()) {
        if (ch == '.') {
            try {
                Thread.sleep(1000)
            } catch (_ignored: InterruptedException) {
                Thread.currentThread().interrupt()
            }

        }
    }
}


object Task {
    //docker run -p 5672:5672 rabbitmq
    @Throws(Exception::class)
    @JvmStatic
    fun main(argv: Array<String>) {

        val factory = ConnectionFactory()
        factory.host = "localhost"
        val connection = factory.newConnection()
        val channel = connection.createChannel()


        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null)

        val message = "Some message3"


        channel.basicPublish("", TASK_QUEUE_NAME,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.toByteArray())
        println(" [x] Sent '$message'")

        channel.close()
        connection.close()
    }
}
