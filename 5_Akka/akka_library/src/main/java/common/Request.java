package common;

import akka.actor.ActorRef;

public abstract class Request {

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
