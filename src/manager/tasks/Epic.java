package manager.tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {
    private final List<Integer> subtaskIds;
    private final Map<Status, Integer> subtaskStatus;
    private Instant endTime;

    private Epic() {
        super();
        this.subtaskIds = new ArrayList<>();
        this.subtaskStatus = new HashMap<>();
        endTime = null;
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW, Duration.ZERO, null);
        this.subtaskIds = new ArrayList<>();
        this.subtaskStatus = new HashMap<>();
        endTime = null;
    }

    public Epic(Epic epic) {
        super(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus(),
                epic.getDuration(), epic.getStartTime());
        this.subtaskIds = new ArrayList<>(epic.getSubtaskIds());
        this.subtaskStatus = new HashMap<>(epic.getSubtaskStatus());
        this.endTime = epic.getEndTime();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public Map<Status, Integer> getSubtaskStatus() {
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

    @Override
    public String toCSV() {
        return getId() + ", "
                + TaskTypes.EPIC + ", "
                + getName() + ", "
                + getStatus() + ", "
                + getDescription() + ", "
                + getDuration().toMinutes() + ", "
                + getStartTime() + ", ";
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
