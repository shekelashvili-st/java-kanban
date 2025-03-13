package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected void sendGetSuccess(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendDeleteSuccess(HttpExchange h) throws IOException {
        h.sendResponseHeaders(200, 0);
        h.close();
    }

    protected void sendCreateUpdateSuccess(HttpExchange h) throws IOException {
        h.sendResponseHeaders(201, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    protected void sendHasCollisions(HttpExchange h) throws IOException {
        h.sendResponseHeaders(406, 0);
        h.close();
    }

    protected void sendServerError(HttpExchange h) throws IOException {
        h.sendResponseHeaders(500, 0);
        h.close();
    }
}
