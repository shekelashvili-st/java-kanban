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

        switch (method) {
            case "GET" -> {
                if (splitPath.length == 3) {
                    int id;
                    Subtask responseSubtask = null;
                    try {
                        id = Integer.parseInt(splitPath[2]);
                        responseSubtask = taskManager.getSubtaskById(id);
                    } catch (NumberFormatException | IdNotPresentException e) {
                        sendNotFound(exchange);
                    }
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
            case "POST" -> {
                if (splitPath.length == 2) {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Subtask postSubtask = gson.fromJson(body, Subtask.class);
                        if (postSubtask.getId() == null) {
                            taskManager.createSubtask(postSubtask);
                        } else {
                            taskManager.updateSubtask(postSubtask);
                        }
                        sendCreateUpdateSuccess(exchange);
                    } catch (IdNotPresentException e) {
                        sendNotFound(exchange);
                    } catch (TaskCollisionException e) {
                        sendHasCollisions(exchange);
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
                        taskManager.deleteSubtaskById(id);
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                    sendDeleteSuccess(exchange);
                } else if (splitPath.length == 2) {
                    taskManager.deleteSubtasks();
                    sendDeleteSuccess(exchange);
                } else {
                    sendNotFound(exchange);
                }
            }
            default -> sendNotFound(exchange);
        }
    }
}
