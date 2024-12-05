import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;
    private int count = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<Task>(tasks.values());
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

    private void increaseCount() {
        count++;
    }
}
