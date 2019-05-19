package common;

import java.io.Serializable;

public class Response implements Serializable {
    private String message = "";
    private double price;
    private RequestType requestType;

    public Response(RequestType requestType) {
        this.requestType = requestType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public RequestType getRequestType() {
        return requestType;
    }
}
