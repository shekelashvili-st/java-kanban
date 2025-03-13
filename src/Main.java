import http.HttpTaskServer;
import manager.Managers;
import manager.taskmanager.TaskManager;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Main {
    private static final TaskManager manager = Managers.getDefault();
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), Instant.now());
        manager.createTask(task1);
        var epic1 = new Epic(null, "Сделать что-то одно", "А потом починить");
        Epic epic1WithId = manager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.DONE, null, null, epic1WithId.getId());
        manager.createSubtask(subtask1);

        HttpTaskServer httpTaskServer = new HttpTaskServer(PORT, manager);
        httpTaskServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
