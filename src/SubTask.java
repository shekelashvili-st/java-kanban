public class SubTask extends Task {
    private final Integer epicId;

    public SubTask(Integer id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(SubTask subtask) {
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
