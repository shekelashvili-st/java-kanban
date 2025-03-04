package manager.taskmanager;

import manager.exception.IdNotPresentException;
import manager.exception.TaskCollisionException;
import manager.historymanager.HistoryManager;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final TreeSet<Task> priorityList;
    private final HistoryManager historyManager;
    protected int count = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        priorityList = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        this.historyManager = historyManager;
    }

    //manager.tasks.Task methods
    @Override
    public List<Task> getTasks() {
        return tasks.values().stream().map(Task::new).toList();
    }

    @Override
    public void deleteTasks() {
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            historyManager.remove(entry.getKey());
            Task task = entry.getValue();
            if (task.getStartTime() != null) {
                priorityList.remove(task);
            }
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task taskInManager = tasks.get(id);
        if (taskInManager == null) {
            return null;
        }
        historyManager.add(new Task(taskInManager));
        return new Task(taskInManager);
    }

    @Override
    public Task createTask(Task task) {
        increaseCount();
        var newTask = new Task(count, task.getName(), task.getDescription(), task.getStatus(),
                task.getDuration(), task.getStartTime());
        tasks.put(newTask.getId(), newTask);
        if (task.getStartTime() != null) {
            if (checkCollisionsInList(priorityList, newTask)) {
                throw new TaskCollisionException("Task collision detected", newTask);
            }
            priorityList.add(newTask);
        }
        return new Task(newTask);
    }

    @Override
    public Task updateTask(Task task) {
        int id = task.getId();
        Task currentTask = tasks.get(id);
        if (currentTask == null) {
            throw new IdNotPresentException(id);
        }
        currentTask.setName(task.getName());
        currentTask.setDescription(task.getDescription());
        currentTask.setStatus(task.getStatus());
        currentTask.setDuration(task.getDuration());

        Instant newStartTime = task.getStartTime();
        // Preserving TreeSet integrity
        if (!Objects.equals(task.getStartTime(), currentTask.getStartTime())) {
            if (currentTask.getStartTime() != null) {
                priorityList.remove(currentTask);
            }
            currentTask.setStartTime(newStartTime);
            if (newStartTime != null) {
                if (checkCollisionsInList(priorityList, currentTask)) {
                    throw new TaskCollisionException("Task collision detected", currentTask);
                }
                priorityList.add(currentTask);
            }
        }
        return new Task(currentTask);
    }

    @Override
    public Task deleteTaskById(int id) {
        historyManager.remove(id);
        Task task = tasks.get(id);
        if (task.getStartTime() != null) {
            priorityList.remove(task);
        }
        return tasks.remove(id);
    }

    //manager.tasks.Epic methods
    @Override
    public List<Epic> getEpics() {
        return epics.values().stream().map(Epic::new).toList();
    }

    @Override
    public void deleteEpics() {
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            historyManager.remove(entry.getKey());
            Subtask subtask = entry.getValue();
            if (subtask.getStartTime() != null) {
                priorityList.remove(subtask);
            }
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epicInManager = epics.get(id);
        if (epicInManager == null) {
            return null;
        }
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
        int id = epic.getId();
        Epic currentEpic = epics.get(id);
        if (currentEpic == null) {
            throw new IdNotPresentException(id);
        }
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
        historyManager.remove(id);
        for (int subtaskId : epic.getSubtaskIds()) {
            deleteSubtaskById(subtaskId);
        }
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        Epic currentEpic = epics.get(id);
        if (currentEpic == null) {
            return null;
        }
        List<Integer> subtaskIds = currentEpic.getSubtaskIds();
        List<Subtask> subtasks = new ArrayList<>();
        for (int subtaskId : subtaskIds) {
            Subtask subtaskCopy = new Subtask(this.subtasks.get(subtaskId));
            subtasks.add(subtaskCopy);
        }
        return subtasks;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        Map<Status, Integer> subtaskStatus = epic.getSubtaskStatus();
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

    private void updateEpicTemporal(Integer epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskIds();

        Instant earliestStartTime = Instant.MAX;
        Instant latestEndTime = Instant.MIN;
        Duration totalDuration = Duration.ZERO;
        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            Instant startTime = subtask.getStartTime();
            Instant endTime = subtask.getEndTime();
            if (startTime != null) {
                if (startTime.isBefore(earliestStartTime)) {
                    earliestStartTime = startTime;
                }
                if (endTime.isAfter(latestEndTime)) {
                    latestEndTime = endTime;
                }
                totalDuration = Duration.between(earliestStartTime, latestEndTime);
            }
        }

        epic.setDuration(totalDuration);
        if (!earliestStartTime.equals(Instant.MAX)) {
            epic.setStartTime(earliestStartTime);
            epic.setEndTime(latestEndTime);
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
        }
    }

    // manager.tasks.Subtask methods
    @Override
    public List<Subtask> getSubtasks() {
        return subtasks.values().stream().map(Subtask::new).toList();
    }

    @Override
    public void deleteSubtasks() {
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            historyManager.remove(entry.getKey());
            Subtask subtask = entry.getValue();
            if (subtask.getStartTime() != null) {
                priorityList.remove(subtask);
            }
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.getSubtaskStatus().clear();
            updateEpicStatus(epic.getId());
            updateEpicTemporal(epic.getId());
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtaskInManager = subtasks.get(id);
        if (subtaskInManager == null) {
            return null;
        }
        historyManager.add(new Subtask(subtaskInManager));
        return new Subtask(subtaskInManager);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            throw new IdNotPresentException(epicId);
        }
        increaseCount();
        var newSubtask = new Subtask(count,
                subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                subtask.getDuration(), subtask.getStartTime(), epicId);
        subtasks.put(newSubtask.getId(), newSubtask);
        if (newSubtask.getStartTime() != null) {
            if (checkCollisionsInList(priorityList, newSubtask)) {
                throw new TaskCollisionException("Task collision detected", newSubtask);
            }
            priorityList.add(newSubtask);
        }
        addSubtaskToEpic(epicId, newSubtask.getId());
        return new Subtask(newSubtask);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask currentSubtask = subtasks.get(subtask.getId());
        if (currentSubtask == null) {
            throw new IdNotPresentException(subtask.getId());
        }
        Epic epic = epics.get(currentSubtask.getEpicId());
        currentSubtask.setName(subtask.getName());
        currentSubtask.setDescription(subtask.getDescription());

        Status newStatus = subtask.getStatus();
        Status oldStatus = currentSubtask.getStatus();
        if (newStatus != oldStatus) {
            epic.getSubtaskStatus().merge(oldStatus, -1, Integer::sum);
            epic.getSubtaskStatus().merge(newStatus, 1, Integer::sum);
            updateEpicStatus(epic.getId());
            currentSubtask.setStatus(newStatus);
        }

        Duration newDuration = subtask.getDuration();
        Instant newStartTime = subtask.getStartTime();
        if (!(Objects.equals(newDuration, currentSubtask.getDuration())
                || Objects.equals(newStartTime, currentSubtask.getStartTime()))) {
            currentSubtask.setDuration(newDuration);
            if (currentSubtask.getStartTime() != null) {
                priorityList.remove(currentSubtask);
            }
            currentSubtask.setStartTime(newStartTime);
            if (newStartTime != null) {
                if (checkCollisionsInList(priorityList, currentSubtask)) {
                    throw new TaskCollisionException("Task collision detected", currentSubtask);
                }
                priorityList.add(currentSubtask);
            }
            updateEpicTemporal(epic.getId());
        }

        return new Subtask(currentSubtask);
    }

    @Override
    public Subtask deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return null;
        }
        historyManager.remove(id);
        if (subtask.getStartTime() != null) {
            priorityList.remove(subtask);
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskStatus().merge(subtask.getStatus(), -1, Integer::sum);
            epic.removeSubtask(subtask.getId());
            updateEpicStatus(epic.getId());
            updateEpicTemporal(epic.getId());
        }
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        // List<Task> shallowTasks = priorityList.stream().toList();
        List<Task> tasks = new ArrayList<>();
        for (Task listTask : priorityList) {
            if (listTask instanceof Subtask subtask) {
                tasks.add(new Subtask(subtask));
            } else {
                tasks.add(new Task(listTask));
            }
        }
        return tasks;
    }

    protected void addSubtaskToEpic(int epicId, int subtaskId) {
        Epic epic = epics.get(epicId);
        Subtask subtask = subtasks.get(subtaskId);
        epic.addSubtask(subtaskId);
        epic.getSubtaskStatus().merge(subtask.getStatus(), 1, Integer::sum);
        updateEpicStatus(epic.getId());
        updateEpicTemporal(epic.getId());
    }

    // Utility methods
    private void increaseCount() {
        count++;
    }

    private boolean checkCollision(Task task, Task other) {
        Instant existingStart = task.getStartTime();
        Instant newStart = other.getStartTime();
        if (existingStart.isAfter(newStart) || existingStart.equals(newStart)) {
            return other.getEndTime().isAfter(existingStart);
        } else {
            return task.getEndTime().isAfter(newStart);
        }
    }

    private boolean checkCollisionsInList(Collection<Task> orderedList, Task task) {
        return orderedList.stream().anyMatch(listTask -> checkCollision(listTask, task));
    }
}
