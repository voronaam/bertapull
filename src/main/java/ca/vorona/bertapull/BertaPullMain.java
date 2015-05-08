package ca.vorona.bertapull;

import static spark.Spark.*;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class BertaPullMain {
    
    private static ConcurrentHashMap<String, BertaConnection> connections = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        // Manage connections
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
            return "Connected\n";
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
            return "Disconnected\n";
        });
        
        get("/connections/", (q, r) -> connections.keySet());
        get("/connections/check", (q, r) -> {
            int alive = 0, dead = 0;
            for(Entry<String, BertaConnection> pair: connections.entrySet()) {
                if(!pair.getValue().check()) {
                    connections.remove(pair.getKey());
                    pair.getValue().disconnect();
                    dead++;
                } else {
                    alive++;
                }
            }
            return String.format("Checked all connections. Found %d alive and %d dead\n", alive, dead);
        });

        exception(LogicException.class, (e, request, response) -> {
            response.status(409);
            response.body(e.getMessage() + "\n");
        });
    }
    
}

