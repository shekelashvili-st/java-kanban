package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exception.IdNotPresentException;
import manager.taskmanager.TaskManager;
import manager.tasks.Epic;
import manager.tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpEpicsHandler extends BaseHttpHandler {

    public HttpEpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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
                default -> sendMethodNotAllowed(exchange);
            }
        } catch (IdNotPresentException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendMethodNotAllowed(exchange);
        } catch (Throwable e) {
            sendServerError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String[] splitPath) throws IOException {
        if (splitPath.length == 3) {
            int id = Integer.parseInt(splitPath[2]);
            Epic responseEpic = taskManager.getEpicById(id);
            String json = gson.toJson(responseEpic);
            sendGetSuccess(exchange, json);
        } else if (splitPath.length == 2) {
            List<Epic> responseEpics = taskManager.getEpics();
            String json = gson.toJson(responseEpics);
            sendGetSuccess(exchange, json);
        } else if (splitPath.length == 4 && splitPath[3].equals("subtasks")) {
            int id = Integer.parseInt(splitPath[2]);
            List<Subtask> responseSubtasks = taskManager.getEpicSubtasks(id);
            String json = gson.toJson(responseSubtasks);
            sendGetSuccess(exchange, json);
        } else {
            sendMethodNotAllowed(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, String[] splitPath) throws IOException {
        if (splitPath.length == 2) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic postEpic = gson.fromJson(body, Epic.class);
            if (postEpic.getId() == null) {
                taskManager.createEpic(postEpic);
            } else {
                taskManager.updateEpic(postEpic);
            }
            sendCreateUpdateSuccess(exchange);
        } else {
            sendMethodNotAllowed(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] splitPath) throws IOException {
        if (splitPath.length == 3) {
            int id = Integer.parseInt(splitPath[2]);
            taskManager.deleteEpicById(id);
            sendDeleteSuccess(exchange);
        } else if (splitPath.length == 2) {
            taskManager.deleteEpics();
            sendDeleteSuccess(exchange);
        } else {
            sendMethodNotAllowed(exchange);
        }
    }
}
