package common;

public abstract class Request {

    private RequestType requestType;

    protected Request(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }
}
