package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.taskmanager.TaskManager;
import manager.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpHistoryHandler extends BaseHttpHandler {

    public HttpHistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");

        if (method.equals("GET") && splitPath.length == 2) {
            List<Task> responseHistory = taskManager.getHistory();
            String json = gson.toJson(responseHistory);
            sendGetSuccess(exchange, json);
        } else {
            sendMethodNotAllowed(exchange);
        }
    }
}
