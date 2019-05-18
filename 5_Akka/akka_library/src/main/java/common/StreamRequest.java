package common;

public class StreamRequest extends Request {

    public StreamRequest(String query) {
        this.requestType = RequestType.STREAM;
        this.query = query;
    }

}
