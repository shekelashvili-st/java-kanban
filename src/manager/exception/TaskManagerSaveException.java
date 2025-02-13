package manager.exception;

import java.nio.file.Path;

public class TaskManagerSaveException extends RuntimeException {
    public final Path filename;

    public TaskManagerSaveException(String message, Path filename) {
        super(message);
        this.filename = filename;
    }
}
