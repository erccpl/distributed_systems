package server;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import common.SearchRequest;

public class SearchDbActor extends AbstractLoggingActor {

    private String dbAddress;
    private String title;
    private ActorRef client;

    public SearchDbActor(String dbAddress, String title, ActorRef client) {
        this.dbAddress = dbAddress;
        this.title = title;
        this.client = client;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchRequest.class, r -> {


                    String line = searchDb(dbAddress);


                    if(!line.equals("")) {
                        getSender().tell(line, getSelf());
                    }

                })
                .matchAny(o -> log().info("Received unknown message"))
                .build();
    }

    public String searchDb(String dbAddress) {
        return "AAA, 20:20";
    }


}
