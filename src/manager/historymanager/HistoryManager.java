package manager.historymanager;

import manager.tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    int MAX_SIZE = 10;

    void add(Task task);

    ArrayList<Task> getHistory();
}
