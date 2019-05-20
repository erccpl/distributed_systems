package server;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorInitializationException;
import akka.actor.ActorRef;
import common.RequestType;
import common.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class SearchDbActor extends AbstractLoggingActor {

    public SearchDbActor(String dbAddress, String title, ActorRef gpActor) throws FileNotFoundException, ActorInitializationException {

        double price = 0;

        File dbFile = new File(dbAddress);
        Scanner scanner = new Scanner(dbFile);
        scanner.useDelimiter("\\n");
        while (scanner.hasNext()) {
            String[] currentTitlePrice = scanner.next().split(",");
            if (currentTitlePrice[0].equals(title)) {
                price = Double.parseDouble(currentTitlePrice[1]);
            }
        }
        scanner.close();

        Response response = new Response(RequestType.SEARCH);
        response.setPrice(price);
        response.setMessage(title);
        gpActor.tell(response, getSelf());

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> log().info("Received unknown message"))
                .build();
    }
}
