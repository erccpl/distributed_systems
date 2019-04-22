import org.jgroups.JChannel;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack","true");

        Scanner scanner = new Scanner(System.in);

        try ( JChannel channel = new JChannel()){

            ProtocolStack stack=new ProtocolStack();
            channel.setProtocolStack(stack);
            stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName("224.0.0.1")))
                    .addProtocol(new PING())
                    .addProtocol(new MERGE3())
                    .addProtocol(new FD_SOCK())
                    .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                    .addProtocol(new VERIFY_SUSPECT())
                    .addProtocol(new BARRIER())
                    .addProtocol(new NAKACK2())
                    .addProtocol(new UNICAST3())
                    .addProtocol(new STABLE())
                    .addProtocol(new GMS())
                    .addProtocol(new UFC())
                    .addProtocol(new MFC())
                    .addProtocol(new FRAG2())
                    .addProtocol(new STATE())
                    .addProtocol(new SEQUENCER())
                    .addProtocol(new FLUSH());

            stack.init();
            channel.connect("distributed_hash_map");
            DistributedMap map = new DistributedMap(channel);

            String command;
            String key;
            int value;

            boolean running = true;

            while (running) {
                command = scanner.next();

                switch (command) {

                    case "p":
                        key = scanner.next();
                        value = scanner.nextInt();
                        map.put(key,value);
                        System.out.println(String.format(">> Added new entry "));
                        break;

                    case "r":
                        key = scanner.next();
                        System.out.println(String.format("<< Removing entry for %d", map.remove(key)));
                        break;

                    case "c":
                        key = scanner.next();
                        System.out.println(map.containsKey(key));
                        break;

                    case "show":
                        System.out.println(Arrays.asList(map.getMap()));
                        break;

                    case "g":
                        key = scanner.next();
                        System.out.println(map.get(key));
                        break;

                    case "q":
                        running = false;
                        break;

                    default:
                        System.out.println("incorrect command: " + command);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
