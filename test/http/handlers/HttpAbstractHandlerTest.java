package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskServer;
import http.adapters.DurationAdapter;
import http.adapters.InstantAdapter;
import http.adapters.StatusAdapter;
import manager.Managers;
import manager.taskmanager.TaskManager;
import manager.tasks.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public abstract class HttpAbstractHandlerTest {
    protected final static int PORT = 8080;
    protected static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Status.class, new StatusAdapter())
            .create();
    protected TaskManager taskManager = Managers.getDefault();
    protected HttpTaskServer httpTaskServer = new HttpTaskServer(PORT, taskManager);

    protected HttpAbstractHandlerTest() throws IOException {
    }

    @BeforeEach
    void startServer() {
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        httpTaskServer.start();
    }

    @AfterEach
    void stopServer() {
        httpTaskServer.stop();
    }
}
