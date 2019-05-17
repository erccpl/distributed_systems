package client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import common.OrderRequest;
import common.Request;
import common.SearchRequest;

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
        System.out.println("Ay yooo whats popping");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            } else if (line.equals("s")) {

                Request searchRequest = new SearchRequest("AAA");
                clientActor.tell(searchRequest, null);


            } else if (line.equals("o")) {

                Request orderRequest = new OrderRequest("AAA");
                System.out.println("here" + orderRequest.getRequestType());
                clientActor.tell(orderRequest, null);


            }
        }

        system.terminate();
    }


}
