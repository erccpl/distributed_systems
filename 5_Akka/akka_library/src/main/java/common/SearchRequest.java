package common;

public class SearchRequest extends Request {


    public SearchRequest(String query) {
        this.requestType = RequestType.SEARCH;
        this.query = query;
    }


}
