package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.RoundRobinPool;
import common.Request;
import common.RequestType;
import scala.concurrent.duration.Duration;


import static akka.actor.SupervisorStrategy.restart;

public class ServerActor extends AbstractLoggingActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {

                    //dispatch
                    if (r.getRequestType() == RequestType.SEARCH) {


                    }


                })
                .matchAny(o -> log().debug("Received unknown message"))
                .build();
    }



    @Override
    public void preStart() throws Exception {

        //creat a pool of actors for every type of request
        //search pool
        final ActorRef searchRouter =
                context().actorOf(SearchActor.props()
                        .withRouter(new RoundRobinPool(5)), "search_router");



    }



    private static SupervisorStrategy strategy
            = new AllForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder
            // todo: match arithmetic exception
            .match(ArithmeticException.class, e -> SupervisorStrategy.resume())
            .matchAny(o -> restart())
            .build());


    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }


}
