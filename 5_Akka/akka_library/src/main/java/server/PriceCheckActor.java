package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.pattern.BackoffOpts;
import akka.pattern.BackoffSupervisor;
import common.Request;
import common.RequestType;
import common.Response;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import static akka.actor.SupervisorStrategy.escalate;


public class PriceCheckActor extends AbstractLoggingActor {

    private String title = "";
    private ActorRef client;

    private int notFoundCounter = 0;
    private Boolean bookFound = false;

    private ArrayList<ActorRef> children = new ArrayList<>();
    private ArrayList<String> dbs;

    public PriceCheckActor(ArrayList<String> dbs, ActorRef client) {
        this.dbs = dbs;
        this.client = client;
    }

    private static OneForOneStrategy backoffSupervisorstrategy
            = new OneForOneStrategy(2,
            scala.concurrent.duration.Duration.create("1 minute"),
            DeciderBuilder
                    .match(ActorInitializationException.class, e -> SupervisorStrategy.restart())
                    .matchAny(o -> escalate())
                    .build());

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Request.class, r -> {

                this.title = r.getQuery();

                for (String db : dbs) {

                    UUID uuid = UUID.randomUUID();
                    String randomUUIDString = uuid.toString().substring(0,8);

                    Props childProps = Props.create(SearchDbActor.class, db, title, getSelf());

                    Props supervisorProps =
                        BackoffSupervisor.props(
                                BackoffOpts.onFailure(
                                        childProps,
                                        "dbSearcher::" + randomUUIDString.substring(0,8),
                                        Duration.ofSeconds(1),
                                        Duration.ofSeconds(2),
                                        0.2)
                                .withMaxNrOfRetries(1)
                                .withSupervisorStrategy(backoffSupervisorstrategy)
                        );
                    ActorRef searchActor = context().actorOf(supervisorProps, "supervisor::" + randomUUIDString);
                    context().watch(searchActor);
                    children.add(searchActor);
                }
            })

            .match(Response.class, response -> {
                if(response.getPrice()!=0) {
                    bookFoundHandler(response);
                } else {
                    bookNotFoundHandler(response);
                }
            })

            .match(Terminated.class, t -> {
                System.out.println("***************TERMINATED********************");
                if (!bookFound) {
                    Response response = new Response(RequestType.SEARCH);
                    response.setPrice(-1.0);
                    response.setMessage(this.title);
                    client.tell(response, null);
                }
            })

            .matchAny(o -> log().info("Received unknown message"))
            .build();
    }


    public void bookFoundHandler(Response response) {
        if (bookFound) {
            return;
        }

        bookFound = true;
        for (ActorRef a : children) {
            a.tell(PoisonPill.getInstance(), null);
        }
        getContext().getParent().tell("price found", null);
        client.tell(response, null);

        getSelf().tell(PoisonPill.getInstance(), null);

    }

    public void bookNotFoundHandler(Response response) {
        notFoundCounter += 1;

        if(notFoundCounter == children.size()) {
            getContext().getParent().tell("price not found", null);
            client.tell(response, null);

            getSelf().tell(PoisonPill.getInstance(), null);
        }

    }

}
