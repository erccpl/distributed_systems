package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.RoundRobinPool;
import common.Request;
import common.RequestType;
import common.SearchRequest;
import scala.concurrent.duration.Duration;


import static akka.actor.SupervisorStrategy.restart;

public class ServerActor extends AbstractLoggingActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {

                    //dispatch
                    if (r.getRequestType() == RequestType.SEARCH) {
                        //search dbs
                        context().child("getPrice_router").get().tell(r, getSelf());

                    }
                    else if (r.getRequestType() == RequestType.ORDER) {

                    }
                    else if (r.getRequestType() == RequestType.STREAM) {

                    }
                })

                .match(String.class, s -> {
                    System.out.println(s);

                })

                .matchAny(o -> log().debug("Received unknown message"))
                .build();
    }





    @Override
    public void preStart() throws Exception {

        //creat a pool of actors for every type of request
        //search pool
        final ActorRef getPriceRouter =
                context().actorOf(GetPriceActor.props()
                        .withRouter(new RoundRobinPool(10)
                                .withSupervisorStrategy(searchRouterStrategy())), "getPrice_router");

        log().info("GetPriceActor pool created");
        System.out.println(getSelf().path());

    }



    private static SupervisorStrategy searchRouterStrategy() {
        return new OneForOneStrategy(10,
                Duration.create("1 minute"),
                DeciderBuilder
                        .matchAny(o -> restart())
                        .build()
        );
    }


    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10,
            Duration.create("1 minute"),
            DeciderBuilder
            .match(ArithmeticException.class, e -> SupervisorStrategy.resume())
            .matchAny(o -> restart())
            .build());


    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

}
