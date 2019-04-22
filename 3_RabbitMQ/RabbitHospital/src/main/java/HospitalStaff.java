import com.rabbitmq.client.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HospitalStaff {

    public static void initAdminListener(Channel channel, String LOGGING_EXCHANGE, String queueName, String routingKey) throws Exception {

        //logging exchange
        channel.exchangeDeclare(LOGGING_EXCHANGE, BuiltinExchangeType.TOPIC, false, true, null);

        channel.queueDeclare(queueName, false, true, true, null);
        channel.queueBind(queueName, LOGGING_EXCHANGE, routingKey);
        System.out.println("Created queue: " + queueName);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {

                String message = new String(body, StandardCharsets.UTF_8);

                System.out.println("\u001B[36m" + "Got announcement: " + message + "\u001B[0m");
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(queueName, false, consumer);
    }

}
