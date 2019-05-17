package server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Server {

    public static void main(String[] args) throws Exception {

        // create actor system & actors
        final ActorSystem system = ActorSystem.create("server_system");
        final ActorRef serverActor = system.actorOf(Props.create(ServerActor.class), "server");
        System.out.println(serverActor.path());


        System.out.println("Sever started. Press 'q' to terminate the server");

        // read line & send to actor
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
        }

        // finish
        system.terminate();
    }

}
