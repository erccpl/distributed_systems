package client;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akka.util.ByteString;
import common.Request;
import common.RequestType;
import common.Response;


public class ClientActor extends AbstractLoggingActor {

    private final String remoteServerPath = "akka.tcp://server_system@127.0.0.1:3552/user/server";

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {

                    ActorSelection remoteServerActor = getContext().actorSelection(remoteServerPath);
                    if (r.getRequestType() == RequestType.SEARCH) {
                        remoteServerActor.tell(r, getSelf());

                    } else if (r.getRequestType() == RequestType.ORDER) {
                        remoteServerActor.tell(r, getSelf());

                    } else if (r.getRequestType() == RequestType.STREAM) {
                        remoteServerActor.tell(r, getSelf());
                    }

                })
                .match(Response.class, response -> {
                    if ( response.getRequestType()==RequestType.SEARCH ) {
                        if (response.getPrice()== -1 ) {
                            System.out.println("Database error, try again later");
                        }
                        else if (response.getPrice()==0) {
                            System.out.println("We don't have that book");

                        } else if (response.getPrice()!=0 ){
                            System.out.println("Price for " + response.getMessage() + " is " + response.getPrice());

                        }
                    }

                    if ( response.getRequestType()==RequestType.ORDER ) {
                        System.out.println("Order placed for " + response.getMessage());
                    }

                    if ( response.getRequestType()==RequestType.STREAM) {
                        System.out.println(response.getMessage());
                    }

                })

                .match(ByteString.class, bs -> {

                    System.out.println(bs.utf8String());


                })

                .match(String.class, s -> {
                    System.out.println(s);
                })

                .matchAny(o -> log().info("Received unknown message"))
                .build();
    }
}
