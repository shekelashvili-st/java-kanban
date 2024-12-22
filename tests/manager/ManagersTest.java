package manager;

import manager.taskmanager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    void shouldReturnInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        Assertions.assertNotNull(taskManager.getTasks());
        Assertions.assertNotNull(taskManager.getEpics());
        Assertions.assertNotNull(taskManager.getSubtasks());
        Assertions.assertNotNull(taskManager.getHistory());
    }
}