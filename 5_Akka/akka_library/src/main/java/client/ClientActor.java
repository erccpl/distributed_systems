package client;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import common.Request;
import common.RequestType;
import common.Response;


public class ClientActor extends AbstractLoggingActor {

    private final String remoteServerPath = "akka.tcp://server_system@127.0.0.1:3552/user/server";

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {

                    if (r.getRequestType() == RequestType.SEARCH) {

                        ActorSelection remoteServerActor = getContext().actorSelection(remoteServerPath);
                        remoteServerActor.tell(r, getSelf());

                    } else if (r.getRequestType() == RequestType.ORDER) {

                        ActorSelection remoteServerActor = getContext().actorSelection(remoteServerPath);
                        remoteServerActor.tell(r, getSelf());

                    } else if (r.getRequestType() == RequestType.STREAM) {

                        ActorSelection remoteServerActor = getContext().actorSelection(remoteServerPath);
                        remoteServerActor.tell(r, getSelf());
                    }

                    })
                .match(Response.class, response -> {





                })
                .matchAny(o -> log().info("received unknown message"))
                .build();
    }
}
