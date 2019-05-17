import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.rabbitmq.client.AMQP.BasicProperties;

public class Doctor extends HospitalStaff{

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("Doctor");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //administration:
        String LOGGING_EXCHANGE = "admin";
        initAdminListener(channel, LOGGING_EXCHANGE, "info.doc1", "info.*");

        //hospital exchange
        String EXCHANGE_NAME = "hospital";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, true, null);

        String queueName = "doc1_Q";
        String doc1Routing = "doc1";
        channel.queueDeclare(queueName, false, true, true, null);
        channel.queueBind(queueName, EXCHANGE_NAME, doc1Routing);
        System.out.println("Created queue: " + queueName);


        //Consumer for examination results
        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {

                String message = new String(body, StandardCharsets.UTF_8);

                System.out.println("Received: " + message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        // start listening
        System.out.println("Format: <patient name> <examination>");
        System.out.println("Waiting for messages...");
        channel.basicConsume(queueName, false, consumer);

        //Console: producing messages
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String message = br.readLine();
                String routingKey = Utils.validateArguments(message);
                if (routingKey.equals("knee") || routingKey.equals("hip") || routingKey.equals("elbow")) {
                    System.out.println("Requesting evaluation: " + message);

                    // define where to reply to:
                    BasicProperties props = new BasicProperties
                            .Builder()
                            .replyTo(doc1Routing)
                            .correlationId("doc1")
                            .timestamp(new Date())
                            .build();

                    //publish
                    channel.basicPublish(EXCHANGE_NAME, routingKey, props, message.getBytes(StandardCharsets.UTF_8));
                    channel.basicPublish(LOGGING_EXCHANGE, "log", props, message.getBytes(StandardCharsets.UTF_8));
                } else {
                    System.out.println("We don't specialize in that");
                }
            }
        } finally {
            // close
            channel.close();
            connection.close();
        }
    }


}
