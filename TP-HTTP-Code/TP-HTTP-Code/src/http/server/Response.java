package http.server;

import java.util.HashMap;

public class Response {

    private int code;
    private String status;
    private HashMap<String, String> headers;
    private HashMap<String, String> body;

    public Response(int c, String s){
        this.code = c;
        this.status = s;
        this.headers = new HashMap<String, String>();
        this.body = new HashMap<String, String>();
    }

}
