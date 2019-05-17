package common;

import akka.actor.ActorRef;

import java.io.Serializable;

public abstract class Request implements Serializable {

    protected RequestType requestType;
    protected ActorRef client;

    protected Request(){
        //mandatory default constructor
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public ActorRef getClient() {
        return client;
    }
}
