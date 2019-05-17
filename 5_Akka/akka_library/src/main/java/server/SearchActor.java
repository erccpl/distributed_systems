package server;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class SearchActor extends AbstractLoggingActor {

    //protocol
    private static class Message { }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Message.class, s -> {

                System.out.println("SearchActor works");


            })
            .matchAny(o -> log().info("Received unknown message"))
            .build();
    }


    public static Props props() {
        return Props.create(SearchActor.class);
    }


}
