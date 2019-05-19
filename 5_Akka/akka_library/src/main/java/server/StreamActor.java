package server;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.stream.*;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;
import common.Request;
import scala.Function1;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletionStage;


public class StreamActor extends AbstractLoggingActor {
    private String path;
    private String title;
    private ActorRef client;

    public StreamActor(Request request, String path, ActorRef client) {
        this.title = request.getQuery();
        this.path = path;
        this.client = client;

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> {
                    System.out.println("got to stream reader");

                    try {

                        //final Materializer materializer = ActorMaterializer.create(context().system());
                        final Path file = Paths.get(path);
                        File tmpDir = new File(path);
                        if (!tmpDir.exists()) {
                            System.out.println("No such file");
                            getSelf().tell(PoisonPill.getInstance(), null);
                        }


                        final Function1<Throwable, Supervision.Directive> decider = exc -> {
                            if (exc instanceof NoSuchFileException) {
                                System.out.println("ok so actually got to no file");
                                return Supervision.stop();
                            } else return Supervision.stop();
                        };

                        final Materializer mat =
                                ActorMaterializer.create(
                                    ActorMaterializerSettings.create(context().system())
                                    .withSupervisionStrategy(decider),
                                    context().system());



                        CompletionStage<IOResult> ioResult =
                            FileIO.fromPath(file)
                                .via(
                                    Framing.delimiter(ByteString.fromString("\n"),
                                            1024,
                                            FramingTruncation.ALLOW))
                                .throttle(1, Duration.ofSeconds(1))
                                .to(Sink.actorRef(client, "OK"))
                                .run(mat);


                        client.tell(ioResult, getSelf());


                    } catch (Exception e) {
                        System.out.println("No such book");
                    }

                    //getSelf().tell(PoisonPill.getInstance(), null);

                })
                .matchAny(o -> log().info("Received unknown message"))
                .build();
    }

}
