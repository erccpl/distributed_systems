package client;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import common.Request;
import common.RequestType;


public class ClientActor extends AbstractLoggingActor {

    private final String remoteServerPath = "akka.tcp://server_system@127.0.0.1:3552/user/server";

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {


                    if (r.getRequestType() == RequestType.SEARCH) {

                        System.out.println("got here search");
                        ActorSelection remoteServerActor = getContext().actorSelection(remoteServerPath);
                        remoteServerActor.tell(r, getSelf());

                    } else if (r.getRequestType() == RequestType.ORDER) {
                        System.out.println("got here order");
                        ActorSelection remoteServerActor = getContext().actorSelection(remoteServerPath);
                        remoteServerActor.tell(r, getSelf());

                    } else if (r.getRequestType() == RequestType.STREAM) {
                        System.out.println("got here stream");
                        ActorSelection remoteServerActor = getContext().actorSelection(remoteServerPath);
                        remoteServerActor.tell(r, getSelf());
                    }

                    })
                .matchAny(o -> log().info("received unknown message"))
                .build();
    }
}
