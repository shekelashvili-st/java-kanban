package manager.taskmanager;

import manager.Managers;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileBackedTaskManagerTest {
    private static TaskManager taskManager;
    private static Path tempFile;

    @BeforeEach
    void prepareFileBackedTaskManager() throws IOException {
        tempFile = Files.createTempFile(null, ".tmp");
        tempFile.toFile().deleteOnExit();

        taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), tempFile);

        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW);
        Task task1WithId = taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, epic1WithId.getId());
        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
    }

    @Test
    void deleteTasksShouldUpdateSaveFile() {
        taskManager.deleteTasks();
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void createTaskShouldUpdateSaveFile() {
        Task newTask = new Task(null, "New task", "Description", Status.NEW);

        taskManager.createTask(newTask);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void updateTaskShouldUpdateSaveFile() {
        Task newTask = taskManager.getTaskById(1);
        newTask.setStatus(Status.IN_PROGRESS);

        taskManager.updateTask(newTask);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void deleteTaskByIdShouldUpdateSaveFile() {
        taskManager.deleteTaskById(1);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void deleteEpicsShouldUpdateSaveFile() {
        taskManager.deleteEpics();
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void createEpicShouldUpdateSaveFile() {
        Epic newEpic = new Epic(null, "New epic", "Description");

        taskManager.createEpic(newEpic);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void updateEpicShouldUpdateSaveFile() {
        Epic newEpic = taskManager.getEpicById(2);
        newEpic.setName("Updated name");

        taskManager.updateEpic(newEpic);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void deleteEpicByIdShouldUpdateSaveFile() {
        taskManager.deleteEpicById(2);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void deleteSubtasksShouldUpdateSaveFile() {
        taskManager.deleteSubtasks();
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void createSubtaskShouldUpdateSaveFile() {
        Subtask newSubtask = new Subtask(null, "New subtask", "Description", Status.NEW, 2);

        taskManager.createSubtask(newSubtask);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());

    }

    @Test
    void updateSubtaskShouldUpdateSaveFile() {
        Subtask newSubtask = taskManager.getSubtaskById(3);
        newSubtask.setName("Updated name");

        taskManager.updateSubtask(newSubtask);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void deleteSubtaskByIdShouldUpdateSaveFile() {
        taskManager.deleteSubtaskById(4);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }
}