package server;

import akka.actor.AbstractLoggingActor;
import akka.actor.PoisonPill;
import common.OrderRequest;
import common.Request;
import common.SearchRequest;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class OrderActor extends AbstractLoggingActor {

    private String path;
    private String title;

    public OrderActor(Request request, String path) {
        this.title = request.getQuery();
        this.path = path;

    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRequest.class, r -> {
                    System.out.println("got to file writer");

                    try {

                    BufferedWriter writer = new BufferedWriter(
                            new FileWriter(path, true)
                    );

                    writer.newLine();   //Add new line
                    writer.write(title);
                    writer.close();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    getSelf().tell(PoisonPill.getInstance(), null);

                })
                .matchAny(o -> log().info("Received unknown message"))
                .build();
    }


}
