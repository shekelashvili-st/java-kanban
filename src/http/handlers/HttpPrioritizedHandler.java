package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.taskmanager.TaskManager;
import manager.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpPrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpPrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");

        if (method.equals("GET") && splitPath.length == 2) {
            List<Task> responseTasks = taskManager.getPrioritizedTasks();
            String json = gson.toJson(responseTasks);
            sendGetSuccess(exchange, json);
        } else {
            sendNotFound(exchange);
        }
    }
}
