package manager.tasks;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        this(subtask.getId(),
                subtask.getName(),
                subtask.getDescription(),
                subtask.getStatus(),
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
                + getEpicId();
    }
}
