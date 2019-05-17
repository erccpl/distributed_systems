package common;

public class OrderRequest extends Request {

    public OrderRequest(String query) {
        this.requestType = RequestType.ORDER;
        this.query = query;
    }

}
