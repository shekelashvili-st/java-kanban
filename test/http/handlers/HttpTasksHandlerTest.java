package http.handlers;

import manager.tasks.Status;
import manager.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

class HttpTasksHandlerTest extends HttpAbstractHandlerTest {

    protected HttpTasksHandlerTest() throws IOException {
    }

    @Test
    void shouldReturnTasks() throws IOException, InterruptedException {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        var task2 = new Task(null, "2", "2",
                Status.IN_PROGRESS, Duration.ofMinutes(1), null);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        String AllTasksJson = gson.toJson(taskManager.getTasks());

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(AllTasksJson, response.body());
    }

    @Test
    void shouldReturnTaskById() throws IOException, InterruptedException {
        Instant start = Instant.now();
        var startTask = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        var startTaskWithId = taskManager.createTask(startTask);

        String startTaskJson = gson.toJson(startTaskWithId);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks/" + startTaskWithId.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(startTaskJson, response.body());
    }

    @Test
    void shouldReturnErrorCodeWhenIdDoesntExist() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks/" + 500);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void shouldReturnErrorCodeWhenPathDoesntExist() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks/" + "three/four");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        Assertions.assertEquals(405, response.statusCode());
    }

    @Test
    void shouldDeleteAllTasks() throws IOException, InterruptedException {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        var task2 = new Task(null, "2", "2",
                Status.IN_PROGRESS, Duration.ofMinutes(1), null);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(List.of(), taskManager.getTasks());
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        var task2 = new Task(null, "2", "2",
                Status.IN_PROGRESS, Duration.ofMinutes(1), null);
        var task1WithId = taskManager.createTask(task1);
        var task2WithId = taskManager.createTask(task2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks/" + task1WithId.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(List.of(task2WithId), taskManager.getTasks());
    }

    @Test
    void shouldReturnSuccessCodeWhenDeletingNonExistentTask() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks/" + 100);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void shouldPostNewTask() throws IOException, InterruptedException {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        var task1CopyWithId = new Task(1, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        String taskJson = gson.toJson(task1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(List.of(task1CopyWithId), taskManager.getTasks());
    }

    @Test
    void shouldReturnErrorCodeAndNotAddTaskWhenNewTaskHasCollisions() throws IOException, InterruptedException {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        var task1WithId = taskManager.createTask(task1);
        var task2 = new Task(null, "2", "2",
                Status.IN_PROGRESS, Duration.ofMinutes(1), start.minusSeconds(580));

        String taskJson = gson.toJson(task2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(List.of(task1WithId), taskManager.getTasks());
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        var task1WithId = taskManager.createTask(task1);
        var task2 = new Task(null, "2", "2",
                Status.IN_PROGRESS, Duration.ofMinutes(1), start.minusSeconds(1000));
        taskManager.createTask(task2);
        var updatedTask2 = new Task(2, "2", "2",
                Status.IN_PROGRESS, Duration.ofMinutes(1), start.minusSeconds(1200));

        String taskJson = gson.toJson(updatedTask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(List.of(task1WithId, updatedTask2), taskManager.getTasks());
    }

    @Test
    void shouldReturnErrorCodeAndNotUpdateTaskWhenUpdatedTaskHasCollisions() throws IOException, InterruptedException {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        var task1WithId = taskManager.createTask(task1);
        var task2 = new Task(null, "2", "2",
                Status.IN_PROGRESS, Duration.ofMinutes(1), start.minusSeconds(1000));
        var task2WithId = taskManager.createTask(task2);
        var updatedTask2 = new Task(2, "2", "2",
                Status.IN_PROGRESS, Duration.ofMinutes(1), start.minusSeconds(580));

        String taskJson = gson.toJson(updatedTask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(List.of(task1WithId, task2WithId), taskManager.getTasks());
    }
}