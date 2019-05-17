package server;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import common.SearchRequest;

public class SearchDbActor extends AbstractLoggingActor {

    private String dbAddress;
    private String title;
    private ActorRef gpActor;

    public SearchDbActor(String dbAddress, String title, ActorRef gpActor) {
        this.dbAddress = dbAddress;
        this.title = title;
        this.gpActor = gpActor;

        //should be done in a Future, asynchronously


        String line2 = searchDb(dbAddress);

        context().watch(gpActor);
        if (gpActor.isTerminated())
        gpActor.tell(line2, getSelf());


    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchRequest.class, r -> {
//
//
//                    String line = searchDb(dbAddress);
//
//
//                    if(!line.equals("")) {
//                        gpActor.tell(line, getSelf());
//                    }

                })
                .matchAny(o -> log().info("Received unknown message"))
                .build();
    }

    public String searchDb(String dbAddress) {
        return "AAA, 20:20";
    }


}
