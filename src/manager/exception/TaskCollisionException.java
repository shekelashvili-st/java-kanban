package manager.exception;

import manager.tasks.Task;

public class TaskCollisionException extends RuntimeException {
    public final Task task;

    public TaskCollisionException(String message, Task task) {
        super(message);
        this.task = task;
    }
}
