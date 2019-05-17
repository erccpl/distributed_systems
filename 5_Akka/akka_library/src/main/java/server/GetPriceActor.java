package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.pattern.BackoffOpts;
import akka.pattern.BackoffSupervisor;
import common.SearchRequest;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;

import static akka.actor.SupervisorStrategy.escalate;


public class GetPriceActor extends AbstractLoggingActor {

    //Poison pill should trigger the restart; no actor should be removed
    //from pool, only get restarted

    private ActorRef client = null;
    private String title = "";
    private String line = "";

    String[] dbs = {"/Users/eric/dev/sr/5_Akka/akka_library/src/main/resources/db1.txt",
    "/Users/eric/dev/sr/5_Akka/akka_library/src/main/resources/db2.txt"};


    private static OneForOneStrategy strategy
            = new OneForOneStrategy(10,
            scala.concurrent.duration.Duration.create("1 minute"),
            DeciderBuilder
                    .match(ArithmeticException.class, e -> SupervisorStrategy.restart())
                    .matchAny(o -> escalate())
                    .build());


    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SearchRequest.class, r -> {
                this.client = r.getClient();
                this.title = r.getQuery();

                System.out.println("GOT HEREs");

                for (String db : dbs) {

                    Props searchProps = Props.create(SearchDbActor.class, db, title, client);
                    final FiniteDuration tenSeconds = FiniteDuration.apply(10, "seconds");

                    Props supervisorProps =
                        BackoffSupervisor.props(
                                BackoffOpts.onStop(
                                        searchProps,
                                        "db::" + db,
                                        Duration.ofSeconds(3),
                                        Duration.ofSeconds(30),
                                        0.2)
                                .withAutoReset(tenSeconds)
                                .withSupervisorStrategy(strategy)
                        );
                    ActorRef searchActor = context().actorOf(supervisorProps, "searchDbActor:" + db);
                    searchActor.tell(r, getSelf());
                }
            })


            .match(String.class, s -> {
                this.line = s;
            })


            .matchAny(o -> log().info("Received unknown message"))
            .build();
    }



    public static Props props() {
        return Props.create(GetPriceActor.class);
    }

    public double getPrice(String db) {
        return 0;
    }


}
