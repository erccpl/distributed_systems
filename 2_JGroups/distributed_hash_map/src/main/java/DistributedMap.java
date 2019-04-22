import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import java.util.HashMap;

import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.*;
import java.util.List;


public class DistributedMap extends ReceiverAdapter implements SimpleStringMap {

    private final JChannel channel;
    private HashMap<String, Integer> hashMap = new HashMap<>();

    public DistributedMap(JChannel channel) {
        this.channel = channel;
        this.channel.setReceiver(this);
        this.channel.setDiscardOwnMessages(true);

        try {

            this.channel.getState(null, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Integer> getMap() {
        return this.hashMap;
    }

    /* Hashmap methods -------------------------------------------------------------------------------------------*/

    public Integer get(String key) {
        return hashMap.get(key);
    }

    public boolean containsKey(String key) {
        return hashMap.containsKey(key);
    }

    public void put(String key, Integer value) {
        try {
            channel.send(new Message(null, null, new InsertMessage(key, value)));
            hashMap.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer remove(String key) {
        try {
            channel.send(new Message(null, null, new RemoveMessage(key)));
            return hashMap.remove(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*-----------------------------------------------------------------------------------------*/

    /**
     * Classes representing the two messages needed to handle inserting/removing
     * from the coordinated HashTable
     */
    private static class InsertMessage implements Serializable {
        final String key;
        final Integer value;

        InsertMessage(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class RemoveMessage implements Serializable {
        final String key;

        RemoveMessage(String key) {
            this.key = key;
        }
    }


    public void receive(Message msg) {
        Object obj = msg.getObject();
        System.out.println("Received message from " + msg.getSrc() + " - " + obj);

        if(obj instanceof InsertMessage) {
            InsertMessage incoming = (InsertMessage) obj;
            hashMap.put(incoming.key, incoming.value);
        }
        else if(obj instanceof RemoveMessage) {
            RemoveMessage incoming = (RemoveMessage) obj;
            hashMap.remove(incoming.key);
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized (hashMap) {
            Util.objectToStream(hashMap, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        synchronized(hashMap) {
            HashMap<String, Integer> newHashMap = (HashMap<String, Integer>) Util.objectFromStream(new DataInputStream(input));
            synchronized(hashMap) {
                hashMap.clear();
                hashMap.putAll(newHashMap);
            }
        }
    }


    /**
     * The following code deals with merging partitions
     *
     * The Primary Partition Approach
     * Source: http://www.jgroups.org/manual/index.html#HandlingNetworkPartitions
     *
     * The primary partition approach is simple: on merging, one subgroup is designated as the primary partition and all
     * others as non-primary partitions. The members in the primary partition don’t do anything, whereas the members in
     * the non-primary partitions need to drop their state and re-initialize their state from fresh state obtained from
     * a member of the primary partition.
     *
     */

    public void viewAccepted(View view) {
        if (view instanceof MergeView) {
            handleView(channel, view);
        }
    }

    public static void handleView(JChannel channel, View view) {
        if(view instanceof MergeView) {
            ViewHandler handler = new ViewHandler(channel, (MergeView) view);
            handler.start();
        }

    }

    public static class ViewHandler extends Thread {
        JChannel ch;
        MergeView view;

        private ViewHandler(JChannel ch, MergeView view) {
            this.ch = ch;
            this.view = view;
        }

        public void run() {
            List<View> subgroups = view.getSubgroups();
            View tmp_view = subgroups.get(0); // picks the first
            Address local_addr = ch.getAddress();

            if(!tmp_view.getMembers().contains(local_addr)) {
                System.out.println("Not member of the new primary partition (" + tmp_view + "), will re-acquire the state");
                try {
                    ch.getState(null, 30000);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Not member of the new primary partition (" + tmp_view + "), will do nothing");
            }
        }
    }
}


