package manager.taskmanager;

import manager.tasks.Epic;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    //manager.tasks.Task methods
    ArrayList<Task> getTasks();

    void deleteTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTaskById(int id);

    //manager.tasks.Epic methods
    ArrayList<Epic> getEpics();

    void deleteEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic deleteEpicById(int id);

    ArrayList<Subtask> getEpicSubtasks(int id);

    // manager.tasks.Subtask methods
    ArrayList<Subtask> getSubtasks();

    void deleteSubtasks();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    Subtask deleteSubtaskById(int id);

    // manager.historymanager.HistoryManager methods
    ArrayList<Task> getHistory();
}
