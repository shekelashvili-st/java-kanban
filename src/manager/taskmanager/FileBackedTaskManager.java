package manager.taskmanager;

import manager.exception.TaskManagerLoadException;
import manager.exception.TaskManagerSaveException;
import manager.historymanager.HistoryManager;
import manager.tasks.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path backupFile;

    public FileBackedTaskManager(HistoryManager historyManager, Path filename) {
        super(historyManager);
        backupFile = filename;
    }

    public static FileBackedTaskManager loadFromFile(HistoryManager historyManager, Path file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);

        try {
            List<String> lines = Files.readAllLines(file);
            int maxId = 0;
            for (String line : lines) {
                String[] tokens = line.split(", ");
                Integer id = Integer.parseInt(tokens[0]);
                TaskTypes type = Enum.valueOf(TaskTypes.class, tokens[1]);
                String name = tokens[2];
                String description = tokens[4];
                Status status = Enum.valueOf(Status.class, tokens[3]);

                if (id > maxId) {
                    maxId = id;
                }
                switch (type) {
                    case TASK -> {
                        Task task = new Task(id, name, description, status);
                        manager.tasks.put(task.getId(), task);
                    }
                    case EPIC -> {
                        Epic epic = new Epic(id, name, description);
                        manager.epics.put(epic.getId(), epic);
                    }
                    case SUBTASK -> {
                        Integer epicId = Integer.parseInt(tokens[5]);
                        Subtask subtask = new Subtask(id, name, description,
                                status, epicId);
                        manager.subtasks.put(subtask.getId(), subtask);
                        manager.addSubtaskToEpic(subtask.getEpicId(), subtask.getId());
                    }
                }
            }
            manager.count = maxId;
        } catch (IOException e) {
            throw new TaskManagerLoadException("Failed to load task manager state from file", file);
        }
        return manager;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Task deleteTaskById(int id) {
        Task deletedTask = super.deleteTaskById(id);
        save();
        return deletedTask;
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic deletedEpic = super.deleteEpicById(id);
        save();
        return deletedEpic;
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Subtask deleteSubtaskById(int id) {
        Subtask deletedSubtask = super.deleteSubtaskById(id);
        save();
        return deletedSubtask;
    }

    private void save() {
        try (var out = new PrintWriter(backupFile.toFile())) {
            for (Task t : getTasks()) {
                out.println(t.toCSV());
            }
            for (Epic t : getEpics()) {
                out.println(t.toCSV());
            }
            for (Subtask t : getSubtasks()) {
                out.println(t.toCSV());
            }
        } catch (IOException e) {
            throw new TaskManagerSaveException("Failed to save task manager state to file", backupFile);
        }
    }

}
