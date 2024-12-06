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
}
