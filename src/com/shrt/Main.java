package src.com.shrt;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import src.com.shrt.handler.HttpRequestHandler;
import src.com.shrt.handler.PostgreSQL;

public class Main {
    public static void main(String[] args) throws IOException {
        PostgreSQL db;
        try {
            db = new PostgreSQL(
                    "jdbc:postgresql://localhost:5432/local",
                    "local",
                    "local");
        } catch (Exception e) {
            System.out.println("db creating error");
            e.printStackTrace();
            return;
        }

        int port = 8080; // Port to listen on

        HttpRequestHandler handler = new HttpRequestHandler(db);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", handler);
        server.start();

        System.out.println("Server is running on port " + port);
    }
}
