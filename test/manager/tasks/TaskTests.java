package manager.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTests {
    @Test
    void TasksShouldBeEqualIfSameId() {
        var task1 = new Task(1, "Сделать что-то одно", "А потом починить", Status.NEW, null, null);
        var task2 = new Task(1, "Сделать что-то второе", "Ничего не сломать", Status.IN_PROGRESS, null, null);

        Assertions.assertEquals(task1, task2);
    }

    @Test
    void TaskSubclassesShouldBeEqualIfSameId() {
        var task1 = new Task(1, "Сделать что-то одно", "А потом починить", Status.NEW, null, null);
        var subtask1 = new Subtask(1, "subtask1", "subtask1", Status.NEW, null, null, null);
        var epic1 = new Epic(1, "epic1", "epic1");

        Assertions.assertEquals(task1, subtask1);
        Assertions.assertEquals(task1, epic1);
    }
}