package manager.taskmanager;

import manager.tasks.Epic;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.util.List;

public interface TaskManager {
    //manager.tasks.Task methods
    List<Task> getTasks();

    void deleteTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTaskById(int id);

    //manager.tasks.Epic methods
    List<Epic> getEpics();

    void deleteEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic deleteEpicById(int id);

    List<Subtask> getEpicSubtasks(int id);

    // manager.tasks.Subtask methods
    List<Subtask> getSubtasks();

    void deleteSubtasks();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    Subtask deleteSubtaskById(int id);

    // manager.historymanager.HistoryManager methods
    List<Task> getHistory();

    // prioritized output method
    List<Task> getPrioritizedTasks();
}
