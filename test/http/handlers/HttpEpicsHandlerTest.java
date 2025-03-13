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

class HttpEpicsHandlerTest extends HttpAbstractHandlerTest {

    protected HttpEpicsHandlerTest() throws IOException {
    }

    @Test
    void shouldReturnEpics() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var epic2 = new Epic(null, "Большой эпик 2", "Без подзадач");
        taskManager.createEpic(epic2);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        String AllEpicsJson = gson.toJson(taskManager.getEpics());

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(AllEpicsJson, response.body());
    }

    @Test
    void shouldReturnEpicById() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        String EpicJson = gson.toJson(taskManager.getEpicById(epic1WithId.getId()));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics/" + epic1WithId.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(EpicJson, response.body());
    }

    @Test
    void shouldReturnErrorCodeWhenIdDoesntExist() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics/" + 500);
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
            URI url = URI.create("http://localhost:" + PORT + "/epics/" + "three/f123our");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteAllEpicsAndSubtasks() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var epic2 = new Epic(null, "Большой эпик 2", "Без подзадач");
        taskManager.createEpic(epic2);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(List.of(), taskManager.getSubtasks());
        Assertions.assertEquals(List.of(), taskManager.getEpics());
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var epic2 = new Epic(null, "Большой эпик 2", "Без подзадач");
        Epic epic2WithId = taskManager.createEpic(epic2);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics/" + epic1WithId.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(List.of(epic2WithId), taskManager.getEpics());
        Assertions.assertEquals(List.of(), taskManager.getSubtasks());
    }

    @Test
    void shouldReturnSuccessCodeWhenDeletingNonExistentEpic() throws IOException, InterruptedException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics/" + 100);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnEpicSubtasks() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        String SubtasksJson = gson.toJson(taskManager.getEpicSubtasks(epic1WithId.getId()));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics/" + epic1WithId.getId() + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(SubtasksJson, response.body());
    }

    @Test
    void shouldPostNewEpic() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        var epic1WithId = new Epic(1, "Большой эпик 1", "Из двух подзадач");
        String epicJson = gson.toJson(epic1);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(List.of(epic1WithId), taskManager.getEpics());
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        taskManager.createEpic(epic1);
        var epic1Updated = new Epic(1, "Updated", "non");

        String epicJson = gson.toJson(epic1Updated);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(List.of(epic1Updated), taskManager.getEpics());
    }
}