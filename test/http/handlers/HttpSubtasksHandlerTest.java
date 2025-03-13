package http.handlers;

import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
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

class HttpSubtasksHandlerTest extends HttpAbstractHandlerTest {

    protected HttpSubtasksHandlerTest() throws IOException {
    }

    @Test
    void shouldReturnSubtasks() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        String AllSubtasksJson = gson.toJson(taskManager.getSubtasks());

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(AllSubtasksJson, response.body());
    }

    @Test
    void shouldReturnSubtaskById() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        var subtask1WithId = taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        String subtaskJson = gson.toJson(subtask1WithId);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks/" + subtask1WithId.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subtaskJson, response.body());
    }

    @Test
    void shouldReturnErrorCodeWhenIdDoesntExist() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks/" + 500);
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
            URI url = URI.create("http://localhost:" + PORT + "/subtasks/" + "three/four");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteAllSubtasks() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(List.of(), taskManager.getSubtasks());
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        var subtask1WithId = taskManager.createSubtask(subtask1);
        var subtask2WithId = taskManager.createSubtask(subtask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks/" + subtask1WithId.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(List.of(subtask2WithId), taskManager.getSubtasks());
    }

    @Test
    void shouldReturnSuccessCodeWhenDeletingNonExistentTask() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks/" + 100);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void shouldPostNewSubtask() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask1CopyWithId = new Subtask(2, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());

        String subtaskJson = gson.toJson(subtask1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(List.of(subtask1CopyWithId), taskManager.getSubtasks());
    }

    @Test
    void shouldReturnErrorCodeAndNotAddSubtaskWhenNewTaskHasCollisions() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start, epic1WithId.getId());
        var subtask1WithId = taskManager.createSubtask(subtask1);

        String subtaskJson = gson.toJson(subtask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(List.of(subtask1WithId), taskManager.getSubtasks());
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        var updatedSubtask1 = new Subtask(2, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(10), start, epic1WithId.getId());

        String subtaskJson = gson.toJson(updatedSubtask1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(List.of(updatedSubtask1), taskManager.getSubtasks());
    }

    @Test
    void shouldReturnErrorCodeAndNotUpdateSubtaskWhenUpdatedSubtaskHasCollisions() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        var subtask1WithId = taskManager.createSubtask(subtask1);
        var subtask2WithId = taskManager.createSubtask(subtask2);
        var updatedSubtask2 = new Subtask(3, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start, epic1WithId.getId());

        String subtaskJson = gson.toJson(updatedSubtask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(List.of(subtask1WithId, subtask2WithId), taskManager.getSubtasks());
    }
}