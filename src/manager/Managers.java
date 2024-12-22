package manager;

import manager.historymanager.HistoryManager;
import manager.historymanager.InMemoryHistoryManager;
import manager.taskmanager.InMemoryTaskManager;
import manager.taskmanager.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
