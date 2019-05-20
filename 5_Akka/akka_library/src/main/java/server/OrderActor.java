package server;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import common.Request;
import common.RequestType;
import common.Response;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class OrderActor extends AbstractLoggingActor {

    private String path;
    private String title;
    private ActorRef client;

    public OrderActor(Request request, String path, ActorRef client) {
        this.title = request.getQuery();
        this.path = path;
        this.client = client;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {
                    System.out.println("got to file writer");

                    enterNewOrder();

                    System.out.println("Order placed for " + title);

                    Response response = new Response(RequestType.ORDER);
                    response.setMessage(title);
                    client.tell(response, null);

                    getSelf().tell(PoisonPill.getInstance(), null);

                })
                .matchAny(o -> log().info("Received unknown message"))
                .build();
    }

    private synchronized void enterNewOrder() throws Exception {

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(path, true));

            writer.newLine();
            writer.write(title);
            writer.close();
    }

}
