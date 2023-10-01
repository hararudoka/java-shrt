package src.com.shrt.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpRequestHandler implements HttpHandler {
    private PostgreSQL db;

    public HttpRequestHandler(PostgreSQL db) {
        this.db = db;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("POST".equals(requestMethod)) {
            switch (path) {
                case "/short":
                    handleGettingURL(exchange);
                    break;
                case "/url":
                    handleGettingShort(exchange);
                    break;
                default:
                    handleNotFoundRoute(exchange);
                    break;
            }
        } else {
            handleNotFoundRoute(exchange);
        }
    }

    private void handleGettingShort(HttpExchange exchange) throws IOException {
        String shrt = "no such shrt";

        InputStream body = exchange.getRequestBody();

        String requestBody;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(body))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            requestBody = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
            return;
        }

        if (requestBody == "") {
            this.handleBadRequest(exchange);
            return;
        }

        try {
            shrt = this.db.selectShrt(requestBody);
            exchange.sendResponseHeaders(200, shrt.length());
        } catch (Exception e) {
            System.out.println(e);
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
            return;
        }

        OutputStream os = exchange.getResponseBody();
        os.write(shrt.getBytes());
        os.close();
    }

    private void handleGettingURL(HttpExchange exchange) throws IOException {
        String url = "no such url";

        InputStream body = exchange.getRequestBody();

        String requestBody;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(body))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            requestBody = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
            return;
        }

        if (requestBody == "") {
            this.handleBadRequest(exchange);
            return;
        }

        try {
            url = this.db.selectUrl(requestBody);
            exchange.sendResponseHeaders(200, url.length());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
            return;
        }

        OutputStream os = exchange.getResponseBody();
        os.write(url.getBytes());
        os.close();
    }

    private void handleNotFoundRoute(HttpExchange exchange) throws IOException {
        String response = "404 page not found";
        exchange.sendResponseHeaders(404, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleBadRequest(HttpExchange exchange) throws IOException {
        String response = "400 bad request";
        exchange.sendResponseHeaders(400, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
