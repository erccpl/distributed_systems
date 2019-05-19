package common;

import akka.actor.ActorRef;

import java.io.Serializable;

public class Request implements Serializable {

    private RequestType requestType;
    private String query;
    private ActorRef client;

    public Request(String query, RequestType requestType){
        this.query = query;
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public ActorRef getClient() {
        return client;
    }

    public String getQuery() {
        return query;
    }

}
