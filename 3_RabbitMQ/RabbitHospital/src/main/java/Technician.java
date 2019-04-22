import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Technician extends HospitalStaff {

    static private int getRandomIntegerInRange(int start, int end) {
        double randomDouble = Math.random();
        randomDouble = randomDouble * end + start;
        return (int) randomDouble;
    }

    public static void main(String[] argv) throws Exception {

        //info
        System.out.println("Technician");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);

        //logging queue
        String LOGGING_EXCHANGE = "admin";
        initAdminListener(channel, LOGGING_EXCHANGE,"info.tech1", "info.*");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter specializations: ");
        String keys = br.readLine();
        String spec1 = keys.split(" ")[0];
        String spec2 = keys.split(" ")[1];


        //exchange
        String EXCHANGE_NAME = "hospital";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, true, null);

        channel.queueDeclare(spec1, false, false, true, null);
        channel.queueBind(spec1, EXCHANGE_NAME, spec1);
        System.out.println("Created queue: " + spec1);

        channel.queueDeclare(spec2, false, false, true, null);
        channel.queueBind(spec2, EXCHANGE_NAME, spec2);
        System.out.println("Created queue: " + spec2);



        // consumer (message handling)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {

                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("Performing evaluation for: " + message);

                int timeToSleep = getRandomIntegerInRange(1,5);
                try {
                    System.out.println("Processing request ...");
                    Thread.sleep(timeToSleep * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(message + " done");
                channel.basicAck(envelope.getDeliveryTag(), false);

                BasicProperties props = new BasicProperties
                        .Builder()
                        .timestamp(new Date())
                        .contentType(properties.getReplyTo())
                        .correlationId("tech1")
                        .build();

                //formulate response and publish
                String response = "Evaluation for " + message + " has result: " + timeToSleep;
                channel.basicPublish(EXCHANGE_NAME, properties.getReplyTo(), props, response.getBytes(StandardCharsets.UTF_8));
                channel.basicPublish(LOGGING_EXCHANGE, "log", props, response.getBytes(StandardCharsets.UTF_8));
            }
        };

        // start listening
        System.out.println("Waiting for messages...");

        channel.basicConsume(spec1, false, consumer);
        channel.basicConsume(spec2, false, consumer);
    }
}
