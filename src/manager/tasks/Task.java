package manager.tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Task {
    private final Integer id;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private Instant startTime;

    Task() {
        this.id = null;
        this.name = null;
        this.description = null;
        this.status = Status.NEW;
        this.duration = Duration.ZERO;
        this.startTime = null;
    }

    public Task(Integer id, String name, String description, Status status,
                Duration duration, Instant startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration == null ? Duration.ZERO : duration;
        this.startTime = startTime;
    }

    public Task(Task task) {
        this(task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getDuration(),
                task.getStartTime());
    }

    @Override
    public boolean equals(Object o) {
        // Since id is unique for tasks and all subclasses
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "manager.tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public String toCSV() {
        return getId() + ", "
                + TaskTypes.TASK + ", "
                + getName() + ", "
                + getStatus() + ", "
                + getDescription() + ", "
                + getDuration().toMinutes() + ", "
                + getStartTime() + ", ";
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration == null ? Duration.ZERO : duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return startTime == null ? null : startTime.plus(duration);
    }
}
