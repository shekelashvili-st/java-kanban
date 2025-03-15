package manager.tasks;

import java.time.Duration;
import java.time.Instant;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(Integer id, String name, String description, Status status,
                   Duration duration, Instant startTime, Integer epicId) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    private Subtask() {
        super();
        epicId = null;
    }

    public Subtask(Subtask subtask) {
        this(subtask.getId(),
                subtask.getName(),
                subtask.getDescription(),
                subtask.getStatus(),
                subtask.getDuration(),
                subtask.getStartTime(),
                subtask.getEpicId());
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "manager.tasks.Subtask{" +
                "epicId=" + epicId +
                "} " + super.toString();
    }

    @Override
    public String toCSV() {
        return getId() + ", "
                + TaskTypes.SUBTASK + ", "
                + getName() + ", "
                + getStatus() + ", "
                + getDescription() + ", "
                + getDuration().toMinutes() + ", "
                + getStartTime() + ", "
                + getEpicId();
    }
}
