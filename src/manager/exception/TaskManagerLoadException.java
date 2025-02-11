package manager.exception;

import java.nio.file.Path;

public class TaskManagerLoadException extends RuntimeException {
    public final Path filename;

    public TaskManagerLoadException(String message, Path filename) {
        super(message);
        this.filename = filename;
    }
}
