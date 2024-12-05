import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus());
        this.subtaskIds = new ArrayList<>(epic.getSubtaskIds());
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}
