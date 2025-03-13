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
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpEpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");

        switch (method) {
            case "GET" -> {
                if (splitPath.length == 3) {
                    int id;
                    Epic responseEpic = null;
                    try {
                        id = Integer.parseInt(splitPath[2]);
                        responseEpic = taskManager.getEpicById(id);
                    } catch (NumberFormatException | IdNotPresentException e) {
                        sendNotFound(exchange);
                    }
                    String json = gson.toJson(responseEpic);
                    sendGetSuccess(exchange, json);
                } else if (splitPath.length == 2) {
                    List<Epic> responseEpics = taskManager.getEpics();
                    String json = gson.toJson(responseEpics);
                    sendGetSuccess(exchange, json);
                } else if (splitPath.length == 4 && splitPath[3].equals("subtasks")) {
                    int id = -1;
                    try {
                        id = Integer.parseInt(splitPath[2]);
                    } catch (NumberFormatException | IdNotPresentException e) {
                        sendNotFound(exchange);
                    }
                    List<Subtask> responseSubtasks = taskManager.getEpicSubtasks(id);
                    String json = gson.toJson(responseSubtasks);
                    sendGetSuccess(exchange, json);
                } else {
                    sendNotFound(exchange);
                }
            }
            case "POST" -> {
                if (splitPath.length == 2) {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Epic postEpic = gson.fromJson(body, Epic.class);
                        if (postEpic.getId() == null) {
                            taskManager.createEpic(postEpic);
                        } else {
                            taskManager.updateEpic(postEpic);
                        }
                        sendCreateUpdateSuccess(exchange);
                    } catch (IdNotPresentException e) {
                        sendNotFound(exchange);
                    } catch (Throwable e) {
                        sendServerError(exchange);
                    }
                } else {
                    sendNotFound(exchange);
                }
            }
            case "DELETE" -> {
                if (splitPath.length == 3) {
                    int id;
                    try {
                        id = Integer.parseInt(splitPath[2]);
                        taskManager.deleteEpicById(id);
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                    sendDeleteSuccess(exchange);
                } else if (splitPath.length == 2) {
                    taskManager.deleteEpics();
                    sendDeleteSuccess(exchange);
                } else {
                    sendNotFound(exchange);
                }
            }
            default -> sendNotFound(exchange);
        }
    }
}
