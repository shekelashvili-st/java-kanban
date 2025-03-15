package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import manager.taskmanager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer httpServer;

    public HttpTaskServer(int port, TaskManager manager, Gson gson) throws IOException {
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", port), 0);
        httpServer.createContext("/tasks", new HttpTasksHandler(manager, gson));
        httpServer.createContext("/subtasks", new HttpSubtasksHandler(manager, gson));
        httpServer.createContext("/epics", new HttpEpicsHandler(manager, gson));
        httpServer.createContext("/history", new HttpHistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new HttpPrioritizedHandler(manager, gson));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
