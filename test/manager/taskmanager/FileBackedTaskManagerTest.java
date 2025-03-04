package manager.taskmanager;

import manager.Managers;
import manager.exception.TaskManagerLoadException;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {
    private static Path tempFile;

    @BeforeEach
    void init() throws IOException {
        prepareTaskManager();
    }

    void prepareTaskManager() throws IOException {
        tempFile = Files.createTempFile(null, ".tmp");
        tempFile.toFile().deleteOnExit();

        taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), tempFile);
    }

    @Test
    void savingEmptyManagerShouldResultInEmptyFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteTasks();
        taskManager.deleteEpics();

        Assertions.assertEquals(0, tempFile.toFile().length());
    }

    @Test
    void loadingEmptyFileShouldReturnEmptyManager() throws IOException {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Path tempFile2 = Files.createTempFile(null, ".tmp");
        tempFile2.toFile().deleteOnExit();
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile2);

        Assertions.assertTrue(newManager.getTasks().isEmpty());
        Assertions.assertTrue(newManager.getSubtasks().isEmpty());
        Assertions.assertTrue(newManager.getEpics().isEmpty());
    }

    @Test
    void deleteTasksShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteTasks();
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void createTaskShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Task newTask = new Task(null, "New task", "Description", Status.NEW, null, null);

        taskManager.createTask(newTask);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void updateTaskShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
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
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteTaskById(1);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void deleteEpicsShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteEpics();
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void createEpicShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic newEpic = new Epic(null, "New epic", "Description");

        taskManager.createEpic(newEpic);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void updateEpicShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
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
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteEpicById(2);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void deleteSubtasksShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtasks();
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void createSubtaskShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Subtask newSubtask = new Subtask(null, "New subtask", "Description", Status.NEW, null, null, 2);

        taskManager.createSubtask(newSubtask);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());

    }

    @Test
    void updateSubtaskShouldUpdateSaveFile() {
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
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
        Instant start = Instant.now();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, Duration.ofMinutes(10), start);
        taskManager.createTask(task1);
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, Duration.ofMinutes(10L), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, start.plusSeconds(6000L), epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskById(4);
        TaskManager newManager = Managers.loadTaskManagerFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
    }

    @Test
    void shouldThrowUserExceptionsWhenLoadingInvalidData() throws IOException {
        Path tempFileInvalid = Files.createTempFile(null, ".tmp");
        tempFileInvalid.toFile().deleteOnExit();
        var out = new DataOutputStream(new FileOutputStream(String.valueOf(tempFileInvalid)));
        out.writeDouble(12312312312.123);
        out.close();

        Assertions.assertThrows(TaskManagerLoadException.class,
                () -> FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), tempFileInvalid));
    }

    @Test
    void shouldThrowUserExceptionsWhenFileDoesntExist() {
        Assertions.assertThrows(TaskManagerLoadException.class,
                () -> FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), Path.of("wrong_file.txt")));
    }
}