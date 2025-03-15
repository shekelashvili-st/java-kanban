package http.handlers;

import com.google.gson.Gson;
import http.HttpTaskServer;
import manager.Managers;
import manager.taskmanager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public abstract class HttpAbstractHandlerTest {
    protected final static int PORT = 8080;
    protected static final Gson gson = Managers.getGson();
    protected final TaskManager taskManager = Managers.getDefault();
    protected final HttpTaskServer httpTaskServer = new HttpTaskServer(PORT, taskManager, gson);

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
