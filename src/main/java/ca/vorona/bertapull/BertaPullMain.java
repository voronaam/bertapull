package ca.vorona.bertapull;

import static spark.Spark.*;

public class BertaPullMain {

    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}

