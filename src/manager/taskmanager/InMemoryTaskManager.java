package manager.taskmanager;

import manager.historymanager.HistoryManager;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private int count = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    //manager.tasks.Task methods
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task taskInManager = tasks.get(id);
        historyManager.add(new Task(taskInManager));
        return new Task(taskInManager);
    }

    @Override
    public Task createTask(Task task) {
        increaseCount();
        var newTask = new Task(count, task.getName(), task.getDescription(), task.getStatus());
        tasks.put(newTask.getId(), newTask);
        return new Task(newTask);
    }

    @Override
    public Task updateTask(Task task) {
        Task currentTask = tasks.get(task.getId());
        currentTask.setName(task.getName());
        currentTask.setDescription(task.getDescription());
        currentTask.setStatus(task.getStatus());
        return new Task(currentTask);
    }

    @Override
    public Task deleteTaskById(int id) {
        return tasks.remove(id);
    }

    //manager.tasks.Epic methods
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epicInManager = epics.get(id);
        historyManager.add(new Epic(epicInManager));
        return new Epic(epicInManager);
    }

    @Override
    public Epic createEpic(Epic epic) {
        increaseCount();
        var newEpic = new Epic(count, epic.getName(), epic.getDescription());
        epics.put(newEpic.getId(), newEpic);
        return new Epic(newEpic);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
        return new Epic(currentEpic);
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            return null;
        }
        for (int subtaskId : epic.getSubtaskIds()) {
            deleteSubtaskById(subtaskId);
        }
        return epic;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int id) {
        Epic currentEpic = epics.get(id);
        ArrayList<Integer> subtaskIds = currentEpic.getSubtaskIds();
        var subtasks = new ArrayList<Subtask>();
        for (int subtaskId : subtaskIds) {
            Subtask subtaskCopy = new Subtask(this.subtasks.get(subtaskId));
            subtasks.add(subtaskCopy);
        }
        return subtasks;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        HashMap<Status, Integer> subtaskStatus = epic.getSubtaskStatus();
        int numNew = subtaskStatus.getOrDefault(Status.NEW, 0);
        int numInProgress = subtaskStatus.getOrDefault(Status.IN_PROGRESS, 0);
        int numDone = subtaskStatus.getOrDefault(Status.DONE, 0);

        if (numInProgress == 0 && numDone == 0) {
            epic.setStatus(Status.NEW);
        } else if (numNew == 0 && numInProgress == 0) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // manager.tasks.Subtask methods
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.getSubtaskStatus().clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtaskInManager = subtasks.get(id);
        historyManager.add(new Subtask(subtaskInManager));
        return new Subtask(subtaskInManager);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        increaseCount();
        var newSubtask = new Subtask(count,
                subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        subtasks.put(newSubtask.getId(), newSubtask);
        addSubtaskToEpic(newSubtask.getEpicId(), newSubtask.getId());
        return new Subtask(newSubtask);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask currentSubtask = subtasks.get(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        currentSubtask.setName(subtask.getName());
        currentSubtask.setDescription(subtask.getDescription());
        if (subtask.getStatus() != currentSubtask.getStatus()) {
            epic.getSubtaskStatus().merge(currentSubtask.getStatus(), -1, Integer::sum);
            epic.getSubtaskStatus().merge(subtask.getStatus(), 1, Integer::sum);
            updateEpicStatus(epic.getId());
            currentSubtask.setStatus(subtask.getStatus());
        }
        return new Subtask(currentSubtask);
    }

    @Override
    public Subtask deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return null;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskStatus().merge(subtask.getStatus(), -1, Integer::sum);
        epic.removeSubtask(subtask.getId());
        updateEpicStatus(epic.getId());
        return subtask;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void addSubtaskToEpic(int epicId, int subtaskId) {
        Epic epic = epics.get(epicId);
        Subtask subtask = subtasks.get(subtaskId);
        epic.addSubtask(subtaskId);
        epic.getSubtaskStatus().merge(subtask.getStatus(), 1, Integer::sum);
        updateEpicStatus(epic.getId());
    }

    // Utility methods
    private void increaseCount() {
        count++;
    }
}
