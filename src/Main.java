import manager.Managers;
import manager.taskmanager.TaskManager;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(null, "New task 1", "Description 1", Status.NEW);
        Task task2 = new Task(null, "New task 2", "Description 2", Status.NEW);
        Epic epic1 = new Epic(null, "New epic 1", "Description 1");
        Subtask subtask1 = new Subtask(null, "New subtask 1", "Description 1", Status.NEW, 3);
        Subtask subtask2 = new Subtask(null, "New subtask 2", "Description 2", Status.NEW, 3);
        Subtask subtask3 = new Subtask(null, "New subtask 3", "Description 3", Status.NEW, 3);
        Epic epic2 = new Epic(null, "New epic 2", "Description 2");

        var task1Id = manager.createTask(task1);
        var task2Id = manager.createTask(task2);
        var epic1Id = manager.createEpic(epic1);
        var epic2Id = manager.createEpic(epic2);
        var subtask1Id = manager.createSubtask(subtask1);
        var subtask2Id = manager.createSubtask(subtask2);
        var subtask3Id = manager.createSubtask(subtask3);

        manager.getSubtaskById(subtask1Id.getId());
        manager.getTaskById(task1Id.getId());
        manager.getTaskById(task2Id.getId());
        manager.getSubtaskById(subtask1Id.getId());
        manager.getSubtaskById(subtask2Id.getId());
        manager.getSubtaskById(subtask3Id.getId());
        manager.getSubtaskById(subtask1Id.getId());
        manager.getEpicById(epic1Id.getId());
        manager.getEpicById(epic2Id.getId());
        manager.getTaskById(task2Id.getId());
        manager.getTaskById(task1Id.getId());
        manager.getEpicById(epic1Id.getId());

        // No duplicates
        List<Task> history = manager.getHistory();
        Set<Task> historyNoDup = new HashSet<>(history);
        if (historyNoDup.size() == history.size()) {
            System.out.println("No duplicates");
        }

        // Deleted task is removed from history
        manager.deleteSubtaskById(subtask1Id.getId());
        manager.deleteEpicById(epic2Id.getId());
        manager.deleteTaskById(task2Id.getId());
        history = manager.getHistory();
        if (historyNoDup.size() == history.size() + 3) {
            System.out.println("Tasks are removed");
        }

        // Epic is deleted with its subtasks
        manager.deleteEpicById(epic1Id.getId());
        history = manager.getHistory();
        if (history.size() == 1) {
            System.out.println("Epic and its subtask are removed");
        }
    }
}
