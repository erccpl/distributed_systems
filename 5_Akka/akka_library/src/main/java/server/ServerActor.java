package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import common.Request;
import common.RequestType;
import scala.concurrent.duration.Duration;


import java.util.UUID;

import static akka.actor.SupervisorStrategy.restart;

public class ServerActor extends AbstractLoggingActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {

                    UUID uuid = UUID.randomUUID();
                    String randomName = uuid.toString().substring(0,8);

                    //dispatch
                    if (r.getRequestType() == RequestType.SEARCH) {
                        //search dbs
                        //context().child("getPrice_router").get().tell(r, getSelf());

                        context().actorOf(PriceCheckActor.props(), "PriceCheckActor::"+randomName);
                        context().child("PriceCheckActor::"+randomName).get().tell(r, getSelf());

                    }
                    else if (r.getRequestType() == RequestType.ORDER) {

                        context().actorOf(Props.create(OrderActor.class, r,"orders.txt"), "OrderActor::"+randomName);
                        context().child("OrderActor::"+randomName).get().tell(r, getSelf());

                    }
                    else if (r.getRequestType() == RequestType.STREAM) {

                        context().actorOf(Props.create(StreamActor.class, r, "pan-tadeusz.txt", getSender()), "StreamActor::"+randomName);
                        context().child("StreamActor::"+randomName).get().tell(r, getSelf());

                    }
                })

                .match(String.class, s -> {
                    System.out.println("In server: " + s);

                })

                .matchAny(o -> log().debug("Received unknown message"))
                .build();
    }





    @Override
    public void preStart() throws Exception {

        //creat a pool of actors for every type of request
        //search pool
//        final ActorRef getPriceRouter =
//                context().actorOf(PriceCheckActor.props()
//                        .withRouter(new RoundRobinPool(10)
//                                .withSupervisorStrategy(searchRouterStrategy())), "getPrice_router");
//
//        log().info("PriceCheckActor pool created");
//        System.out.println(getSelf().path());

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
