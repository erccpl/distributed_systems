package client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import common.Request;
import common.RequestType;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Client {

    public static void main(String[] args) throws Exception {

        // config
        File configFile = new File("client.conf");
        Config config = ConfigFactory.parseFile(configFile);

        // create actor system & actors
        final ActorSystem system = ActorSystem.create("client_system", config);
        final ActorRef clientActor = system.actorOf(Props.create(ClientActor.class), "client");

        // interaction
        System.out.println("Welcome to the Library. Enter your request like so:");
        System.out.println("Check if book is available: c <title>");
        System.out.println("Order a book: o <title>");
        System.out.println("Stream a book: s <title>");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            } else if (line.equals("c")) {

                Request searchRequest = new Request("AAA", RequestType.SEARCH);
                clientActor.tell(searchRequest, null);

            } else if (line.equals("o")) {

                Request orderRequest = new Request("AAA", RequestType.ORDER);
                clientActor.tell(orderRequest, null);

            } else if (line.equals("s")) {

                Request streamRequest = new Request("Pan Tadeusz", RequestType.STREAM);
                clientActor.tell(streamRequest, null);

            }
        }

        system.terminate();
    }


}
