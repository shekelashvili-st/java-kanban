package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.adapters.DurationAdapter;
import http.adapters.InstantAdapter;
import http.adapters.StatusAdapter;
import http.handlers.*;
import manager.taskmanager.TaskManager;
import manager.tasks.Status;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class HttpTaskServer {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Status.class, new StatusAdapter())
            .create();
    private final HttpServer httpServer;

    public HttpTaskServer(int port, TaskManager manager) throws IOException {
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
