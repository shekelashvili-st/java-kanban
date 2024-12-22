package manager.taskmanager;

import manager.Managers;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    void prepareInMemoryManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldAddAndReturnCorrectTask() {
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW);

        Task task1WithId = taskManager.createTask(task1);
        Task returnedTask1 = taskManager.getTaskById(1);

        Assertions.assertTrue(Objects.equals(task1WithId.getId(), returnedTask1.getId())
                && Objects.equals(task1WithId.getName(), returnedTask1.getName())
                && Objects.equals(task1WithId.getDescription(), returnedTask1.getDescription())
                && task1WithId.getStatus() == returnedTask1.getStatus());
        Assertions.assertTrue(!Objects.equals(task1.getId(), returnedTask1.getId())
                && Objects.equals(task1.getName(), returnedTask1.getName())
                && Objects.equals(task1.getDescription(), returnedTask1.getDescription())
                && task1.getStatus() == returnedTask1.getStatus());
    }

    @Test
    void shouldAddAndReturnCorrectEpic() {
        var epic1 = new Epic(null, "Сделать что-то одно", "А потом починить");

        Epic epic1WithId = taskManager.createEpic(epic1);
        Epic returnedEpic1 = taskManager.getEpicById(1);

        Assertions.assertTrue(Objects.equals(epic1WithId.getId(), returnedEpic1.getId())
                && Objects.equals(epic1WithId.getName(), returnedEpic1.getName())
                && Objects.equals(epic1WithId.getDescription(), returnedEpic1.getDescription())
                && epic1WithId.getStatus() == returnedEpic1.getStatus());
        Assertions.assertTrue(!Objects.equals(epic1.getId(), returnedEpic1.getId())
                && Objects.equals(epic1.getName(), returnedEpic1.getName())
                && Objects.equals(epic1.getDescription(), returnedEpic1.getDescription())
                && epic1.getStatus() == returnedEpic1.getStatus());
    }

    @Test
    void shouldAddAndReturnCorrectSubtask() {
        var epic1 = new Epic(null, "Сделать что-то одно", "А потом починить");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать что-то одно", "А потом починить", Status.IN_PROGRESS, 1);

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask returnedSubtask1 = taskManager.getSubtaskById(2);

        Assertions.assertTrue(Objects.equals(subtask1WithId.getId(), returnedSubtask1.getId())
                && Objects.equals(subtask1WithId.getName(), returnedSubtask1.getName())
                && Objects.equals(subtask1WithId.getDescription(), returnedSubtask1.getDescription())
                && subtask1WithId.getStatus() == returnedSubtask1.getStatus());
        Assertions.assertTrue(!Objects.equals(subtask1.getId(), returnedSubtask1.getId())
                && Objects.equals(subtask1.getName(), returnedSubtask1.getName())
                && Objects.equals(subtask1.getDescription(), returnedSubtask1.getDescription())
                && subtask1.getStatus() == returnedSubtask1.getStatus());
    }

    @Test
    void shouldNotChangeTaskStateInManagerFromOutside() {
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW);

        Task task1WithId = taskManager.createTask(task1);
        task1.setName("Changed name");
        task1WithId.setStatus(Status.IN_PROGRESS);
        Task returnedTask1 = taskManager.getTaskById(1);

        Assertions.assertNotEquals("Changed name", returnedTask1.getName());
        Assertions.assertNotEquals(Status.IN_PROGRESS, returnedTask1.getStatus());
    }

    @Test
    void shouldNotChangeEpicStateInManagerFromOutside() {
        var epic1 = new Epic(null, "Сделать что-то одно", "А потом починить");

        Epic epic1WithId = taskManager.createEpic(epic1);
        epic1.setName("Changed name");
        epic1WithId.setDescription("Changed description");
        Epic returnedEpic = taskManager.getEpicById(1);

        Assertions.assertNotEquals("Changed name", returnedEpic.getName());
        Assertions.assertNotEquals("Changed description", returnedEpic.getDescription());
    }

    @Test
    void shouldNotChangeSubtaskStateInManagerFromOutside() {
        var epic1 = new Epic(null, "Сделать что-то одно", "А потом починить");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать что-то одно", "А потом починить", Status.IN_PROGRESS, 1);

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask returnedSubtask1 = taskManager.getSubtaskById(2);
        subtask1.setName("Changed name");
        subtask1WithId.setDescription("Changed description");

        Assertions.assertNotEquals("Changed name", returnedSubtask1.getName());
        Assertions.assertNotEquals("Changed description", returnedSubtask1.getDescription());
    }

    @Test
    void shouldSaveOldTaskDataInHistory() {
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW);
        var taskChanged = new Task(1, "Changed name", "А потом починить", Status.NEW);

        Task task1WithId = taskManager.createTask(task1);
        Task returnedTask1 = taskManager.getTaskById(1);
        taskManager.updateTask(taskChanged);
        ArrayList<Task> history = taskManager.getHistory();
        Task taskInHistory = history.getFirst();

        assertTrue(Objects.equals(taskInHistory.getId(), taskChanged.getId())
                && !Objects.equals(taskInHistory.getName(), taskChanged.getName())
                && Objects.equals(taskInHistory.getDescription(), taskChanged.getDescription())
                && taskInHistory.getStatus() == taskChanged.getStatus());
    }

    @Test
    void shouldNotUpdateEpicStatusManually() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, epic1WithId.getId());
        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);

        epic1WithId.setStatus(Status.NEW);
        taskManager.updateEpic(epic1WithId);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertNotEquals(Status.NEW, epicInManager.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenAddingSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.IN_PROGRESS, epicInManager.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenDeletingSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1WithId.getId());
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.DONE, epicInManager.getStatus());
    }
}