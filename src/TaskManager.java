import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private int count = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Task methods
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return new Task(tasks.get(id));
    }

    public Task createTask(Task task) {
        increaseCount();
        var newTask = new Task(count, task.getName(), task.getDescription(), task.getStatus());
        tasks.put(newTask.getId(), newTask);
        return new Task(newTask);
    }

    public Task updateTask(Task task) {
        Task currentTask = tasks.get(task.getId());
        currentTask.setName(task.getName());
        currentTask.setDescription(task.getDescription());
        currentTask.setStatus(task.getStatus());
        return new Task(currentTask);
    }

    public Task deleteTaskById(int id) {
        return tasks.remove(id);
    }

    //Epic methods
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtaskIds()) {
                deleteSubtaskById(subtaskId);
            }
        }
        epics.clear();
    }

    public Epic getEpicById(int id) {
        return new Epic(epics.get(id));
    }

    public Epic createEpic(Epic epic) {
        increaseCount();
        var newEpic = new Epic(count, epic.getName(), epic.getDescription());
        epics.put(newEpic.getId(), newEpic);
        return new Epic(newEpic);
    }

    public Epic updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
        return new Epic(currentEpic);
    }

    public Epic deleteEpicById(int id) {
        for (int subtaskId : epics.get(id).getSubtaskIds()) {
            deleteSubtaskById(subtaskId);
        }
        return epics.remove(id);
    }
    
    public ArrayList<Subtask> getEpicSubtasks(int id) {
        Epic currentEpic = epics.get(id);
        if (currentEpic == null) {
            return null;
        }
        ArrayList<Integer> subtaskIds = currentEpic.getSubtaskIds();
        var subtasks = new ArrayList<Subtask>();
        for (int subtaskId : subtaskIds) {
            Subtask subtaskCopy = new Subtask(this.subtasks.get(subtaskId));
            subtasks.add(subtaskCopy);
        }
        return subtasks;
    }

    private void addSubtaskToEpic(int epicId, int subtaskId) {
        Epic epic = epics.get(epicId);
        Subtask subtask = subtasks.get(subtaskId);
        epic.addSubtask(subtaskId);
    }

    private void updateEpicStatus(int epicId) {
        ArrayList<Subtask> subtasks = getEpicSubtasks(epicId);
        Status status = Status.NEW;
        for (var subtask : subtasks) {

        }
    }

    // Subtask methods
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteSubtasks() {
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        return new Subtask(subtasks.get(id));
    }

    public Subtask createSubtask(Subtask subtask) {
        increaseCount();
        var newSubtask = new Subtask(count,
                subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        subtasks.put(newSubtask.getId(), newSubtask);

        addSubtaskToEpic(newSubtask.getEpicId(),newSubtask.getId());
        return new Subtask(newSubtask);
    }

    public Subtask updateSubtask(Subtask subtask) {
        Subtask currentSubtask = subtasks.get(subtask.getId());
        currentSubtask.setName(subtask.getName());
        currentSubtask.setDescription(subtask.getDescription());
        currentSubtask.setStatus(subtask.getStatus());
        return new Subtask(currentSubtask);
    }

    public Subtask deleteSubtaskById(int id) {
        return subtasks.remove(id);
    }

    // Utility methods
    private void increaseCount() {
        count++;
    }
}
