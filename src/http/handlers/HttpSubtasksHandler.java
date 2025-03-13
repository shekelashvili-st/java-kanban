package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exception.IdNotPresentException;
import manager.exception.TaskCollisionException;
import manager.taskmanager.TaskManager;
import manager.tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpSubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpSubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");

        try {
            switch (method) {
                case "GET" -> handleGet(exchange, splitPath);
                case "POST" -> handlePost(exchange, splitPath);
                case "DELETE" -> handleDelete(exchange, splitPath);
                default -> sendNotFound(exchange);
            }
        } catch (NumberFormatException | IdNotPresentException e) {
            sendNotFound(exchange);
        } catch (TaskCollisionException e) {
            sendHasCollisions(exchange);
        } catch (Throwable e) {
            sendServerError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String[] splitPath) throws IOException {
        if (splitPath.length == 3) {
            int id = Integer.parseInt(splitPath[2]);
            Subtask responseSubtask = taskManager.getSubtaskById(id);
            String json = gson.toJson(responseSubtask);
            sendGetSuccess(exchange, json);
        } else if (splitPath.length == 2) {
            List<Subtask> responseSubtasks = taskManager.getSubtasks();
            String json = gson.toJson(responseSubtasks);
            sendGetSuccess(exchange, json);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, String[] splitPath) throws IOException {
        if (splitPath.length == 2) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask postSubtask = gson.fromJson(body, Subtask.class);
            if (postSubtask.getId() == null) {
                taskManager.createSubtask(postSubtask);
            } else {
                taskManager.updateSubtask(postSubtask);
            }
            sendCreateUpdateSuccess(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] splitPath) throws IOException {
        if (splitPath.length == 3) {
            int id = Integer.parseInt(splitPath[2]);
            taskManager.deleteSubtaskById(id);
            sendDeleteSuccess(exchange);
        } else if (splitPath.length == 2) {
            taskManager.deleteSubtasks();
            sendDeleteSuccess(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}
