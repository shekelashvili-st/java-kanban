package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exception.IdNotPresentException;
import manager.exception.TaskCollisionException;
import manager.taskmanager.TaskManager;
import manager.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTasksHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public HttpTasksHandler(TaskManager taskManager, Gson gson) {
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
                    Task responseTask = null;
                    try {
                        id = Integer.parseInt(splitPath[2]);
                        responseTask = taskManager.getTaskById(id);
                    } catch (NumberFormatException | IdNotPresentException e) {
                        sendNotFound(exchange);
                    }
                    String json = gson.toJson(responseTask);
                    sendGetSuccess(exchange, json);
                } else if (splitPath.length == 2) {
                    List<Task> responseTasks = taskManager.getTasks();
                    String json = gson.toJson(responseTasks);
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
                        Task postTask = gson.fromJson(body, Task.class);
                        if (postTask.getId() == null) {
                            taskManager.createTask(postTask);
                        } else {
                            taskManager.updateTask(postTask);
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
                        taskManager.deleteTaskById(id);
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                    sendDeleteSuccess(exchange);
                } else if (splitPath.length == 2) {
                    taskManager.deleteTasks();
                    sendDeleteSuccess(exchange);
                } else {
                    sendNotFound(exchange);
                }
            }
            default -> sendNotFound(exchange);
        }
    }
}
