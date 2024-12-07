package manager.tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private final HashMap<Status, Integer> subtaskStatus;

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtaskIds = new ArrayList<>();
        this.subtaskStatus = new HashMap<>();
    }

    public Epic(Epic epic) {
        super(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus());
        this.subtaskIds = new ArrayList<>(epic.getSubtaskIds());
        this.subtaskStatus = new HashMap<>(epic.getSubtaskStatus());
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public HashMap<Status, Integer> getSubtaskStatus() {
        return subtaskStatus;
    }

    public void addSubtask(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtask(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    @Override
    public String toString() {
        return "manager.tasks.Epic{" +
                "subtaskIds=" + subtaskIds +
                ", subtaskStatus=" + subtaskStatus +
                "} " + super.toString();
    }
}
