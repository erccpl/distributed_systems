import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Admin {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("Admin");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();


        //logging exchange
        String LOGGING_EXCHANGE = "admin";
        channel.exchangeDeclare(LOGGING_EXCHANGE, BuiltinExchangeType.TOPIC, false, true, null);


        String queueName = "admin_Q";
        String loggingRoutingKey = "log";

        channel.queueDeclare(queueName, false, true, true, null);
        channel.queueBind(queueName, LOGGING_EXCHANGE, loggingRoutingKey);
        System.out.println("Created queue: " + queueName);


        //Define consumer, make it start listening
        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {

                String message = new String(body, StandardCharsets.UTF_8);

                if (properties.getContentType() == null) {
                    System.out.println("\u001B[34m" + properties.getTimestamp() + " [" + properties.getCorrelationId() + "]: "+ message);
                } else {
                    System.out.println("\u001B[34m" + properties.getTimestamp() +
                            " [" + properties.getCorrelationId() + "] for [" +
                            properties.getContentType() + "]: " + message);
                }
                channel.basicAck(envelope.getDeliveryTag(), false);


            }
        };

        // start listening
        System.out.println("Waiting for messages...");

        channel.basicConsume(queueName, false, consumer);

        System.out.println("Enter announcement: ");


        //Console: producing messages
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String message = br.readLine();

                // define where to reply to:
                AMQP.BasicProperties props = new AMQP.BasicProperties
                        .Builder()
                        .correlationId("admin")
                        .timestamp(new Date())
                        .build();

                //send announcement
                channel.basicPublish(LOGGING_EXCHANGE, "log", props, message.getBytes(StandardCharsets.UTF_8));
                channel.basicPublish(LOGGING_EXCHANGE, "info.all", props, message.getBytes(StandardCharsets.UTF_8));
            }

        } finally {
            // close
            channel.close();
            connection.close();
        }
    }
}
