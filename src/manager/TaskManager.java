package manager;

import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;

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

    //manager.tasks.Task methods
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

    //manager.tasks.Epic methods
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
        Epic epic = epics.remove(id);
        if (epic == null) {
            return null;
        }
        for (int subtaskId : epic.getSubtaskIds()) {
            deleteSubtaskById(subtaskId);
        }
        return epic;
    }

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
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.getSubtaskStatus().clear();
            updateEpicStatus(epic.getId());
        }
    }

    public Subtask getSubtaskById(int id) {
        return new Subtask(subtasks.get(id));
    }

    public Subtask createSubtask(Subtask subtask) {
        increaseCount();
        var newSubtask = new Subtask(count,
                subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        subtasks.put(newSubtask.getId(), newSubtask);
        addSubtaskToEpic(newSubtask.getEpicId(), newSubtask.getId());
        return new Subtask(newSubtask);
    }

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

    public Subtask deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return null;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskStatus().merge(subtask.getStatus(), -1, Integer::sum);
        updateEpicStatus(epic.getId());
        return subtask;
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
