package rabbitmq.task2;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;


public class Client {

    private static final String TASK_QUEUE_NAME = "task_queue_front";
    private static final String BACK_TASK_QUEUE_NAME = "task_queue_back";

    public static void main(String[] argv)
            throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        final Channel channel2 = connection.createChannel();


        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        channel2.queueDeclare(BACK_TASK_QUEUE_NAME, true, false, false, null);


        final Consumer consumer = new DefaultConsumer(channel2) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                System.out.println(" [x] Received '" + message + "'");

                System.out.println(" [x] Done");
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        boolean autoAck = false;
        channel.basicConsume(BACK_TASK_QUEUE_NAME, autoAck, consumer);

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String message = sc.nextLine() + " " + new Date(System.currentTimeMillis());

            channel.basicPublish("", TASK_QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        sc.close();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        channel.close();
        connection.close();
    }

}
