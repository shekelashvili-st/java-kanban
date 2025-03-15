package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapters.DurationAdapter;
import http.adapters.InstantAdapter;
import http.adapters.StatusAdapter;
import manager.historymanager.HistoryManager;
import manager.historymanager.InMemoryHistoryManager;
import manager.taskmanager.FileBackedTaskManager;
import manager.taskmanager.InMemoryTaskManager;
import manager.taskmanager.TaskManager;
import manager.tasks.Status;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager loadTaskManagerFromFile(Path filename) {
        return FileBackedTaskManager.loadFromFile(getDefaultHistory(), filename);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .create();
    }
}
