package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.pattern.BackoffOpts;
import akka.pattern.BackoffSupervisor;
import common.Request;
import common.Response;
import scala.concurrent.duration.FiniteDuration;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import static akka.actor.SupervisorStrategy.restart;


public class PriceCheckActor extends AbstractLoggingActor {

    private String title = "";
    private String line = "";
    private ActorRef client;

    private int notFoundCounter = 0;
    private int reported = 0;
    private Boolean bookFound = false;

    private ArrayList<ActorRef> children = new ArrayList<>();
    private ArrayList<String> dbs;

    public PriceCheckActor(ArrayList<String> dbs, ActorRef client) {
        this.dbs = dbs;
        this.client = client;
    }

    private static OneForOneStrategy strategy
            = new OneForOneStrategy(10,
            scala.concurrent.duration.Duration.create("1 minute"),
            DeciderBuilder
                    .match(FileNotFoundException.class, e -> SupervisorStrategy.resume())
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

                this.title = r.getQuery();
                reported = dbs.size();

                for (String db : dbs) {

                    UUID uuid = UUID.randomUUID();
                    String randomUUIDString = uuid.toString().substring(0,8);

                    Props childProps = Props.create(SearchDbActor.class, db, title, getSelf());
                    final FiniteDuration tenSeconds = FiniteDuration.apply(10, "seconds");

                    Props supervisorProps =
                        BackoffSupervisor.props(
                                BackoffOpts.onStop(
                                        childProps,
                                        "dbSearcher::" + randomUUIDString.substring(0,8),
                                        Duration.ofSeconds(3),
                                        Duration.ofSeconds(30),
                                        0.2)
                                .withAutoReset(tenSeconds)
                                .withSupervisorStrategy(strategy)
                        );
                    ActorRef searchActor = context().actorOf(supervisorProps, "supervisor::" + randomUUIDString);
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
        getContext().getParent().tell("Found", null);
        client.tell(response, null);

        getSelf().tell(PoisonPill.getInstance(), null);

    }

    public void bookNotFoundHandler(Response response) {
        notFoundCounter += 1;

        if(notFoundCounter == children.size()) {
            getContext().getParent().tell("Not found", null);
            client.tell(response, null);

            getSelf().tell(PoisonPill.getInstance(), null);
        }

    }



}
