import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.adapters.DurationAdapter;
import http.adapters.InstantAdapter;
import http.adapters.StatusAdapter;
import http.handlers.*;
import manager.Managers;
import manager.taskmanager.TaskManager;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class Main {
    private static final int PORT = 8080;
    private static final TaskManager manager = Managers.getDefault();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Status.class, new StatusAdapter())
            .create();

    public static void main(String[] args) throws IOException {

        HttpServer httpServer = HttpServer.create();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), Instant.now());
        manager.createTask(task1);
        var epic1 = new Epic(null, "Сделать что-то одно", "А потом починить");
        Epic epic1WithId = manager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.DONE, null, null, epic1WithId.getId());
        manager.createSubtask(subtask1);

        httpServer.bind(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new HttpTasksHandler(manager, gson));
        httpServer.createContext("/subtasks", new HttpSubtasksHandler(manager, gson));
        httpServer.createContext("/epics", new HttpEpicsHandler(manager, gson));
        httpServer.createContext("/history", new HttpHistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new HttpPrioritizedHandler(manager, gson));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
