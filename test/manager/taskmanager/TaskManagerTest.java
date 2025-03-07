package manager.taskmanager;

import manager.exception.IdNotPresentException;
import manager.exception.TaskCollisionException;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void shouldAddAndReturnCorrectTask() {
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, null, null);

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
        taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать что-то одно", "А потом починить", Status.IN_PROGRESS, null, null, 1);

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
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, null, null);

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
        taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать что-то одно", "А потом починить", Status.IN_PROGRESS, null, null, 1);

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask returnedSubtask1 = taskManager.getSubtaskById(2);
        subtask1.setName("Changed name");
        subtask1WithId.setDescription("Changed description");

        Assertions.assertNotEquals("Changed name", returnedSubtask1.getName());
        Assertions.assertNotEquals("Changed description", returnedSubtask1.getDescription());
    }

    @Test
    void shouldSaveOldTaskDataInHistory() {
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW, null, null);
        var taskChanged = new Task(1, "Changed name", "А потом починить", Status.NEW, null, null);

        taskManager.createTask(task1);
        taskManager.getTaskById(1);
        taskManager.updateTask(taskChanged);
        List<Task> history = taskManager.getHistory();
        Task taskInHistory = history.getFirst();

        Assertions.assertTrue(Objects.equals(taskInHistory.getId(), taskChanged.getId())
                && !Objects.equals(taskInHistory.getName(), taskChanged.getName())
                && Objects.equals(taskInHistory.getDescription(), taskChanged.getDescription())
                && taskInHistory.getStatus() == taskChanged.getStatus());
    }

    @Test
    void shouldNotUpdateEpicStatusManually() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

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
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.IN_PROGRESS, epicInManager.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenModifyingSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        subtask1WithId.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1WithId);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.DONE, epicInManager.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusWhenDeletingSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1WithId.getId());
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.DONE, epicInManager.getStatus());
    }

    @Test
    void shouldUpdateEpicSubtaskIdsWhenDeletingSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1WithId.getId());
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(subtask2WithId.getId(), epicInManager.getSubtaskIds().getFirst());
        Assertions.assertEquals(1, epicInManager.getSubtaskIds().size());
    }

    @Test
    void shouldReturnNullWhenCallingGetWithWrongId() {
        Assertions.assertNull(taskManager.getEpicById(1));
        Assertions.assertNull(taskManager.getTaskById(1));
        Assertions.assertNull(taskManager.getSubtaskById(1));
        Assertions.assertNull(taskManager.getEpicSubtasks(1));
    }

    @Test
    void shouldThrowExceptionWhenCallingUpdateWithWrongId() {
        var task1 = new Task(1, "Сделать что-то одно", "А потом починить", Status.NEW, null, null);
        var epic1 = new Epic(2, "Большой эпик 1", "Из двух подзадач");
        var subtask1 = new Subtask(3, "Сделать что-то одно", "А потом починить", Status.IN_PROGRESS, null, null, 1);


        Assertions.assertThrows(IdNotPresentException.class, () -> taskManager.updateTask(task1));
        Assertions.assertThrows(IdNotPresentException.class, () -> taskManager.updateEpic(epic1));
        Assertions.assertThrows(IdNotPresentException.class, () -> taskManager.updateSubtask(subtask1));
    }

    @Test
    void shouldThrowExceptionWhenCreatingSubtaskWithWrongEpicId() {
        var subtask1 = new Subtask(3, "Сделать что-то одно", "А потом починить", Status.IN_PROGRESS, null, null, 1);

        Assertions.assertThrows(IdNotPresentException.class, () -> taskManager.createSubtask(subtask1));
    }

    @Test
    void shouldRemoveDeletedByIdTasksFromHistory() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        taskManager.getSubtaskById(subtask2WithId.getId());
        taskManager.getSubtaskById(subtask1WithId.getId());
        taskManager.getEpicById(epic1WithId.getId());
        taskManager.deleteSubtaskById(subtask1WithId.getId());

        Assertions.assertEquals(List.of(subtask2WithId, epic1WithId), taskManager.getHistory());
    }

    @Test
    void shouldRemoveConsequentlyDeletedSubtasksFromHistory() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        taskManager.getSubtaskById(subtask2WithId.getId());
        taskManager.getSubtaskById(subtask1WithId.getId());
        taskManager.getEpicById(epic1WithId.getId());
        taskManager.deleteEpicById(epic1WithId.getId());

        Assertions.assertEquals(List.of(), taskManager.getHistory());
    }

    @Test
    void shouldRemoveBatchDeletedSubtasksFromHistory() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        taskManager.getSubtaskById(subtask2WithId.getId());
        taskManager.getSubtaskById(subtask1WithId.getId());
        taskManager.getEpicById(epic1WithId.getId());
        taskManager.deleteSubtasks();

        Assertions.assertEquals(List.of(epic1WithId), taskManager.getHistory());
    }

    @Test
    void shouldRemoveBatchDeletedEpicsAndSubtasksFromHistory() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        taskManager.getSubtaskById(subtask2WithId.getId());
        taskManager.getSubtaskById(subtask1WithId.getId());
        taskManager.getEpicById(epic1WithId.getId());
        taskManager.deleteEpics();

        Assertions.assertEquals(List.of(), taskManager.getHistory());
    }

    @Test
    void shouldCalculateEpicTemporalsWhenAddingSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(10), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(6000L), epic1WithId.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Duration expectedDuration = subtask1.getDuration().plus(subtask2.getDuration());
        Assertions.assertEquals(start, epicInManager.getStartTime());
        Assertions.assertEquals(expectedDuration, epicInManager.getDuration());
        Assertions.assertEquals(subtask2.getEndTime(), epicInManager.getEndTime());
    }

    @Test
    void shouldCalculateEpicTemporalsWhenDeletingSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(10), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(6000L), epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1WithId.getId());
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(start.plusSeconds(6000L), epicInManager.getStartTime());
        Assertions.assertEquals(Duration.ofMinutes(4), epicInManager.getDuration());
        Assertions.assertEquals(subtask2.getEndTime(), epicInManager.getEndTime());
    }

    @Test
    void shouldCalculateEpicTemporalsWhenUpdatingSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(10), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(6000L), epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        subtask1WithId.setDuration(Duration.ofMinutes(2));
        subtask1WithId.setStartTime(start.minusSeconds(500L));
        taskManager.updateSubtask(subtask1WithId);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Duration expectedDuration = subtask1WithId.getDuration().plus(subtask2WithId.getDuration());
        Assertions.assertEquals(start.minusSeconds(500L), epicInManager.getStartTime());
        Assertions.assertEquals(expectedDuration, epicInManager.getDuration());
        Assertions.assertEquals(subtask2.getEndTime(), epicInManager.getEndTime());
    }

    @Test
    void shouldCalculateEpicTemporalsWithNulls() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(10), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, start.plusSeconds(6000L), epic1WithId.getId());

        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        subtask1WithId.setDuration(null);
        subtask1WithId.setStartTime(null);
        taskManager.updateSubtask(subtask1WithId);
        taskManager.deleteSubtaskById(subtask2WithId.getId());
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertNull(epicInManager.getStartTime());
        Assertions.assertEquals(Duration.ZERO, epicInManager.getDuration());
        Assertions.assertEquals(subtask1WithId.getEndTime(), epicInManager.getEndTime());
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(10), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(6000L), epic1WithId.getId());
        var task1 = new Task(null, "Сделать", "1",
                Status.DONE, Duration.ofMinutes(4), start.minusSeconds(6000L));

        Task task1WithId = taskManager.createTask(task1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        subtask1WithId.setDuration(Duration.ofMinutes(2));
        subtask1WithId.setStartTime(start.minusSeconds(500L));
        taskManager.updateSubtask(subtask1WithId);

        Assertions.assertEquals(List.of(task1WithId, subtask1WithId, subtask2WithId), taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldReturnPrioritizedTasksWithNulls() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(10), null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, start.plusSeconds(6000L), epic1WithId.getId());

        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        subtask1WithId.setDuration(Duration.ofMinutes(2));
        taskManager.updateSubtask(subtask1WithId);

        Assertions.assertEquals(List.of(subtask2WithId), taskManager.getPrioritizedTasks());
    }

    /// Specific sprint 8 tests
    @Test
    void shouldCalculateEpicStatusCorrectlyWithNewSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.NEW, null, null, epic1WithId.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.NEW, epicInManager.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusCorrectlyWithDoneSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.DONE, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.DONE, epicInManager.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusCorrectlyWithNewAndDoneSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, null, null, epic1WithId.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.IN_PROGRESS, epicInManager.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusCorrectlyWithInProgressSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(Status.IN_PROGRESS, epicInManager.getStatus());
    }

    @Test
    void shouldThrowExceptionsWhenCollisionsOccur() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Instant start = Instant.now();
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(5), start, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.DONE, Duration.ofMinutes(4), start.plusSeconds(600L), epic1WithId.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        var collidingStart = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(10), start.minusSeconds(580));
        var collidingWhole = new Task(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(2), start);
        var collidingEnd = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, Duration.ofMinutes(2), start.plusSeconds(240), epic1WithId.getId());


        Assertions.assertThrows(TaskCollisionException.class, () -> taskManager.createTask(collidingStart));
        Assertions.assertThrows(TaskCollisionException.class, () -> taskManager.createTask(collidingWhole));
        Assertions.assertThrows(TaskCollisionException.class, () -> taskManager.createTask(collidingEnd));
    }

    @Test
    void shouldReturnEpicSubtasks() {
        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        Epic epic1WithId = taskManager.createEpic(epic1);
        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, null, null, epic1WithId.getId());

        var sub1WithId = taskManager.createSubtask(subtask1);
        var sub2WithId = taskManager.createSubtask(subtask2);
        Epic epicInManager = taskManager.getEpicById(epic1WithId.getId());

        Assertions.assertEquals(List.of(sub1WithId, sub2WithId), taskManager.getEpicSubtasks(epicInManager.getId()));
    }
}
