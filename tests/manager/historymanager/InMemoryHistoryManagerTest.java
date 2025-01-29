package manager.historymanager;

import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;

    @BeforeEach
    public void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddNewTasks() {
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW);
        var epic1 = new Epic(2, "Большой эпик 1", "Из двух подзадач");

        historyManager.add(task1);
        historyManager.add(epic1);

        Assertions.assertEquals(task1, historyManager.getHistory().get(0));
        Assertions.assertEquals(epic1, historyManager.getHistory().get(1));
    }

    @Test
    void shouldUpdateLinksCorrectlyWhenDeletingFromMiddle() {
        var task1 = new Task(1, "Сделать что-то одно", "А потом починить", Status.NEW);
        var epic1 = new Epic(2, "Большой эпик 1", "Из двух подзадач");
        var epic2 = new Epic(4, "Большой эпик 2", "Из двух подзадач");
        var epic3 = new Epic(3, "Большой эпик 3", "Из двух подзадач");

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(epic3);
        historyManager.remove(4);

        Assertions.assertEquals(task1, historyManager.getHistory().get(0));
        Assertions.assertEquals(epic1, historyManager.getHistory().get(1));
        Assertions.assertEquals(epic3, historyManager.getHistory().get(2));
    }

    @Test
    void shouldUpdateLinksCorrectlyWhenDeletingFromHeadAndTail() {
        var task1 = new Task(1, "Сделать что-то одно", "А потом починить", Status.NEW);
        var epic1 = new Epic(2, "Большой эпик 1", "Из двух подзадач");
        var epic2 = new Epic(4, "Большой эпик 2", "Из двух подзадач");
        var epic3 = new Epic(3, "Большой эпик 3", "Из двух подзадач");

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(epic3);
        historyManager.remove(1);
        historyManager.remove(3);

        Assertions.assertEquals(epic1, historyManager.getHistory().get(0));
        Assertions.assertEquals(epic2, historyManager.getHistory().get(1));
    }

    @Test
    void shouldUpdateLinksCorrectlyAfterClearingTheList() {
        var task1 = new Task(1, "Сделать что-то одно", "А потом починить", Status.NEW);
        var epic1 = new Epic(2, "Большой эпик 1", "Из двух подзадач");
        var epic2 = new Epic(4, "Большой эпик 2", "Из двух подзадач");
        var epic3 = new Epic(3, "Большой эпик 3", "Из двух подзадач");

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.add(epic2);
        historyManager.add(epic3);

        Assertions.assertEquals(epic2, historyManager.getHistory().get(0));
        Assertions.assertEquals(epic3, historyManager.getHistory().get(1));
    }

    @Test
    void shouldHandleDuplicatesCorrectly() {
        var task1 = new Task(1, "Сделать что-то одно", "А потом починить", Status.NEW);
        var epic1 = new Epic(2, "Большой эпик 1", "Из двух подзадач");
        var epic2 = new Epic(4, "Большой эпик 2", "Из двух подзадач");

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(task1);
        historyManager.add(task1);

        Assertions.assertEquals(epic1, historyManager.getHistory().get(0));
        Assertions.assertEquals(epic2, historyManager.getHistory().get(1));
        Assertions.assertEquals(task1, historyManager.getHistory().get(2));
    }

    @Test
    void shouldCacheHistory() {
        var task1 = new Task(1, "Сделать что-то одно", "А потом починить", Status.NEW);
        var epic1 = new Epic(2, "Большой эпик 1", "Из двух подзадач");

        historyManager.add(task1);
        historyManager.add(epic1);
        List<Task> history1 = historyManager.getHistory();
        List<Task> history2 = historyManager.getHistory();

        Assertions.assertSame(history1, history2);
    }
}