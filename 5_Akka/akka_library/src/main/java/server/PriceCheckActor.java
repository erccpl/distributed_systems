package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.pattern.BackoffOpts;
import akka.pattern.BackoffSupervisor;
import common.SearchRequest;
import scala.concurrent.duration.FiniteDuration;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import static akka.actor.SupervisorStrategy.restart;


public class PriceCheckActor extends AbstractLoggingActor {

    //Poison pill should trigger the restart; no actor should be removed
    //from pool, only get restarted

    private String title = "";
    private String line = "";

    private int notFoundCounter = 0;
    private int reported = 0;
    private Boolean bookFound = false;

    private ArrayList<ActorRef> children = new ArrayList<>();


    String[] dbs = {"/Users/eric/dev/sr/5_Akka/akka_library/src/main/resources/db1.txt",
    "/Users/eric/dev/sr/5_Akka/akka_library/src/main/resources/db2.txt"};

    String[] stringTable = {"one","two"};

    private static OneForOneStrategy strategy
            = new OneForOneStrategy(10,
            scala.concurrent.duration.Duration.create("1 minute"),
            DeciderBuilder
                    .match(FileNotFoundException.class, e -> SupervisorStrategy.restart())
                    .matchAny(o -> restart())
                    .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SearchRequest.class, r -> {

                this.title = r.getQuery();

                System.out.println("GOT HERE");

                reported = stringTable.length;

                for (String db : stringTable) {

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
                    //context().actorOf(supervisorProps, "supervisor::" + randomUUIDString);

                    //searchActor.tell(r, getSelf());
                }
            })

            .match(String.class, s -> {
                this.line = s;
                System.out.println(line);
                bookFoundHandler();
            })


            .matchAny(o -> log().info("Received unknown message"))
            .build();
    }



    public static Props props() {
        return Props.create(PriceCheckActor.class);
    }

    public void bookFoundHandler() {
        if (bookFound) {
            return;
        }

        bookFound = true;
        for (ActorRef a : children) {
            a.tell(PoisonPill.getInstance(), null);
        }
        getContext().getParent().tell("Found", null);

        getSelf().tell(PoisonPill.getInstance(), null);


    }

    public void bookNotFoundHandler() {
        notFoundCounter += 1;

        if(notFoundCounter == children.size()) {
            getContext().getParent().tell("Not found", null);
            getSelf().tell(PoisonPill.getInstance(), null);
        }


    }

    public double getPrice(String db) {
        return 0;
    }


}
