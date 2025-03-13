package http.handlers;

import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
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

class HttpPrioritizedHandlerTest extends HttpAbstractHandlerTest {

    protected HttpPrioritizedHandlerTest() throws IOException {
    }

    @Test
    void shouldReturnPrioritizedList() throws IOException, InterruptedException {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        var startTask = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(3), start.minusSeconds(580));
        taskManager.createTask(startTask);
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        String prioritizedJson = gson.toJson(prioritized);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:" + PORT + "/prioritized");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(prioritizedJson, response.body());
    }
}