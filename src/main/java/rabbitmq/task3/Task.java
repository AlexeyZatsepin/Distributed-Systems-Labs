package rabbitmq.task3;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Task {

    private static final String TASK_QUEUE_NAME = "task_3_queue";

    public static void main(String[] argv)
            throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Map<String, Object> args = new HashMap<String, Object>();
//        args.put("x-message-ttl", 10000);
        args.put("x-max-length", 5);// если длинна в очереди максимум 5- значит сможем считать только последние 5 сообщений что зашли в очередь


        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, args);

        Scanner sc = new Scanner(System.in);

        String message = sc.nextLine();

        sc.close();


        channel.basicPublish("", TASK_QUEUE_NAME,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
