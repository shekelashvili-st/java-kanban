package manager.taskmanager;

import manager.Managers;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    void init() {
        prepareTaskManager();
    }

    void prepareTaskManager() {
        taskManager = Managers.getDefault();
    }
}

