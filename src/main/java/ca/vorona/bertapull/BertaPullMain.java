package ca.vorona.bertapull;

import static spark.Spark.*;

import java.util.concurrent.ConcurrentHashMap;

public class BertaPullMain {
    
    private static ConcurrentHashMap<String, BertaConnection> connections = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        
        
        get("/hello", (req, res) -> "Hello World");
        get("/connect/:host/:port", (req, res) -> {
            String host = req.params("host");
            int port = Integer.parseInt(req.params("port"));
            String connectionKey = String.format("%s:%d", host, port);
            if(connections.containsKey(connectionKey)) {
                throw new LogicException("Already Connected");
            }
            BertaConnection c = new BertaConnection(host, port);
            c.connect();
            connections.put(connectionKey, c);
            return "Connected";
        });
        
        delete("/disconnect/:host/:port", (req, res) -> {
            String host = req.params("host");
            int port = Integer.parseInt(req.params("port"));
            String connectionKey = String.format("%s:%d", host, port);
            if(!connections.containsKey(connectionKey)) {
                throw new LogicException("Not Connected");
            }
            BertaConnection a = connections.remove(connectionKey);
            a.disconnect();
            return "Disconnected";
        });

        exception(LogicException.class, (e, request, response) -> {
            response.status(409);
            response.body(e.getMessage());
        });
    }
}

