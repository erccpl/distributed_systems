package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.pattern.BackoffOpts;
import akka.pattern.BackoffSupervisor;
import common.SearchRequest;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;


public class GetPriceActor extends AbstractLoggingActor {

    //Poison pill should trigger the restart; no actor should be removed
    //from pool, only get restarted

    private String title = "";
    private String line = "";

    private int counter = 0;
    private Boolean bookFound = false;

    private ArrayList<ActorRef> children = new ArrayList<>();


    String[] dbs = {"/Users/eric/dev/sr/5_Akka/akka_library/src/main/resources/db1.txt",
    "/Users/eric/dev/sr/5_Akka/akka_library/src/main/resources/db2.txt"};

    String[] stringTable = {"one","two"};

    private static OneForOneStrategy strategy
            = new OneForOneStrategy(10,
            scala.concurrent.duration.Duration.create("1 minute"),
            DeciderBuilder
                    .match(ArithmeticException.class, e -> SupervisorStrategy.restart())
                    .matchAny(o -> restart())
                    .build());


    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SearchRequest.class, r -> {

                this.title = r.getQuery();

                System.out.println("GOT HERE");

                for (String db : stringTable) {

                    UUID uuid = UUID.randomUUID();
                    String randomUUIDString = uuid.toString();

                    Props searchProps = Props.create(SearchDbActor.class, db, title, getSelf());
                    final FiniteDuration tenSeconds = FiniteDuration.apply(10, "seconds");

                    Props supervisorProps =
                        BackoffSupervisor.props(
                                BackoffOpts.onStop(
                                        searchProps,
                                        "dbSearcher::" + randomUUIDString,
                                        Duration.ofSeconds(3),
                                        Duration.ofSeconds(30),
                                        0.2)
                                .withAutoReset(tenSeconds)
                                .withSupervisorStrategy(strategy)
                        );
                    ActorRef searchActor = context().actorOf(supervisorProps, "supervisor::" + randomUUIDString);
                    children.add(searchActor);
                    searchActor.tell(r, getSelf());
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
        return Props.create(GetPriceActor.class);
    }

    public void bookFoundHandler() {
        if (bookFound) {
            return;
        }

        bookFound = true;
        for (ActorRef a : children) {
            a.tell(PoisonPill.class, null);
        }
        getContext().getParent().tell("Found", null);
        getSelf().tell(PoisonPill.class, null);

    }

    public void bookNotFoundHandler() {
        counter += 1;

        if(counter == children.size()) {
            getContext().getParent().tell("Not found", null);
            getSelf().tell(PoisonPill.class, null);
        }


    }

    public double getPrice(String db) {
        return 0;
    }


}
