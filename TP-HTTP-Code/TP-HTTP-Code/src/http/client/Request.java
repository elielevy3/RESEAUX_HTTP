package http.client;

import java.util.HashMap;

public class Request {

    public enum HTTP {GET, PUT, DELETE, UPDATE, POST, HEAD}
    public HTTP httpAction;
    public String resource;
    public HashMap<String, String> headers;
    public HashMap<String, String> body;
    
    public Request(HTTP action, String r, HashMap<String, String> b, HashMap<String, String> h){
        this.httpAction = action;
        this.resource = r;
        this.body = b;
        this.headers = h;
    }

    public Request(HTTP action, String r ){
        this(action, r, new HashMap<String, String>(), new HashMap<String, String>());
    }
}
