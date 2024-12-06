import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus());
        this.subtaskIds = new ArrayList<>(epic.getSubtaskIds());
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(int id) {
        subtaskIds.add(id);
    }
}
