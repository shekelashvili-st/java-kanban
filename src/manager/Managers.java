package manager;

import manager.historymanager.HistoryManager;
import manager.historymanager.InMemoryHistoryManager;
import manager.taskmanager.FileBackedTaskManager;
import manager.taskmanager.InMemoryTaskManager;
import manager.taskmanager.TaskManager;

import java.nio.file.Path;

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
}
