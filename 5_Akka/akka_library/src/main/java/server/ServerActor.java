package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import common.Request;
import common.RequestType;
import common.Response;
import scala.concurrent.duration.Duration;
import java.util.ArrayList;
import java.util.UUID;

import static akka.actor.SupervisorStrategy.restart;

public class ServerActor extends AbstractLoggingActor {

    private ArrayList<String> dbAddresses = new ArrayList<>();
    private String orderFile = "orders.txt";

    public ServerActor() {
        this.dbAddresses.add("/Users/eric/dev/sr/5_Akka/akka_library/src/main/resources/db1.txt");
        this.dbAddresses.add("/Users/eric/dev/sr/5_Akka/akka_library/src/main/resources/db2.txt");
    }

    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10,
            Duration.create("1 minute"),
            DeciderBuilder
                    .matchAny(o -> restart())
                    .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {

                    UUID uuid = UUID.randomUUID();
                    String randomName = uuid.toString().substring(0,8);

                    //dispatch
                    if (r.getRequestType() == RequestType.SEARCH) {
                        context().actorOf(Props.create(PriceCheckActor.class, dbAddresses, getSender()), "PriceCheckActor::"+randomName);
                        context().child("PriceCheckActor::"+randomName).get().tell(r, getSelf());
                    }
                    else if (r.getRequestType() == RequestType.ORDER) {
                        context().actorOf(Props.create(OrderActor.class, r, orderFile, getSender()), "OrderActor::"+randomName);
                        context().child("OrderActor::"+randomName).get().tell(r, getSelf());
                    }
                    else if (r.getRequestType() == RequestType.STREAM) {
                        if(!r.getQuery().equals("Pan_Tadeusz")){
                            Response response = new Response(RequestType.STREAM);
                            response.setMessage("We don't have that book available for streaming");
                            getSender().tell(response, null);
                        } else {
                            context().actorOf(Props.create(StreamActor.class, r, "pan-tadeusz.txt", getSender()), "StreamActor::" + randomName);
                            context().child("StreamActor::" + randomName).get().tell(r, getSelf());
                        }
                    }
                })
                .match(String.class, s -> {
                    System.out.println("In server: " + s);
                })
                .matchAny(o -> log().debug("Received unknown message"))
                .build();
    }

}
