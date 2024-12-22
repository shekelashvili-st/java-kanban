package manager.historymanager;

import manager.tasks.Status;
import manager.tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;

    @BeforeEach
    public void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddNewTaskWhenNotFull() {
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW);

        historyManager.add(task1);

        Assertions.assertEquals(task1, historyManager.getHistory().getFirst());
    }

    @Test
    public void shouldDeleteFirstElementIfFull() {
        var task1 = new Task(1, "1", "А потом починить", Status.NEW);
        var task2 = new Task(2, "2", "А потом починить", Status.NEW);
        var task3 = new Task(3, "3", "А потом починить", Status.NEW);
        var task4 = new Task(4, "4", "А потом починить", Status.NEW);
        var task5 = new Task(5, "5", "А потом починить", Status.NEW);
        var task6 = new Task(6, "6", "А потом починить", Status.NEW);
        var task7 = new Task(7, "7", "А потом починить", Status.NEW);
        var task8 = new Task(8, "8", "А потом починить", Status.NEW);
        var task9 = new Task(9, "9", "А потом починить", Status.NEW);
        var task10 = new Task(10, "10", "А потом починить", Status.NEW);
        var newTask = new Task(11, "new", "А потом починить", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);
        historyManager.add(task10);
        historyManager.add(newTask);

        Assertions.assertEquals(task2, historyManager.getHistory().getFirst());
        Assertions.assertEquals(newTask, historyManager.getHistory().getLast());
    }

}