package common;

public class SearchRequest extends Request {

    private String query;

    public SearchRequest(String query) {
        this.requestType = RequestType.SEARCH;
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
