import manager.taskmanager.TaskManager;
import manager.tasks.Epic;
import manager.tasks.Status;
import manager.tasks.Subtask;
import manager.tasks.Task;

public class Main {

    public static void main(String[] args) {
        var taskManager = new TaskManager();
        var task1 = new Task(null, "Сделать что-то одно", "А потом починить", Status.NEW);
        var task2 = new Task(null, "Сделать что-то второе", "Ничего не сломать", Status.IN_PROGRESS);
        Task task1WithId = taskManager.createTask(task1);
        Task task2WithId = taskManager.createTask(task2);

        var epic1 = new Epic(null, "Большой эпик 1", "Из двух подзадач");
        var epic2 = new Epic(null, "Меньший эпик 2", "Из одной позадачи");
        Epic epic1WithId = taskManager.createEpic(epic1);
        Epic epic2WithId = taskManager.createEpic(epic2);

        var subtask1 = new Subtask(null, "Сделать малое одно", "А потом починить",
                Status.NEW, epic1WithId.getId());
        var subtask2 = new Subtask(null, "Сделать малое второе", "Ничего не сломать",
                Status.IN_PROGRESS, epic1WithId.getId());
        var subtask3 = new Subtask(null, "Сделать малое третье", "Ничего не сломать",
                Status.NEW, epic2WithId.getId());
        Subtask subtask1WithId = taskManager.createSubtask(subtask1);
        Subtask subtask2WithId = taskManager.createSubtask(subtask2);
        Subtask subtask3WithId = taskManager.createSubtask(subtask3);

        System.out.println("Tasks:");
        System.out.println(taskManager.getTasks());
        System.out.println("Epics:");
        System.out.println(taskManager.getEpics());
        System.out.println("Subtasks:");
        System.out.println(taskManager.getSubtasks());

        task2WithId.setDescription("Не сломали");
        task2WithId.setStatus(Status.DONE);
        taskManager.updateTask(task2WithId);
        System.out.println("Изменили описание второй задачи:");
        System.out.println(taskManager.getTasks());

        epic1WithId.setStatus(Status.NEW);
        epic1WithId.setDescription("Изменили статус, пока менеджер не смотрит");
        taskManager.updateEpic(epic1WithId);
        System.out.println("Изменили описание первого эпика, \"Изменили\" его статус, вывели по id:");
        System.out.println(taskManager.getEpicById(epic1WithId.getId()));

        subtask1WithId.setStatus(Status.DONE);
        subtask2WithId.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1WithId);
        taskManager.updateSubtask(subtask2WithId);
        taskManager.deleteSubtaskById(subtask3WithId.getId());
        System.out.println("Поменяли статус подзадач 1 и 2 на DONE, удалили 3 подзадачу:");
        System.out.println(taskManager.getSubtasks());

        System.out.println("Изменения в эпиках:");
        System.out.println(taskManager.getEpics());
        taskManager.deleteEpicById(epic2WithId.getId());
        System.out.println("Удалили второй эпик:");
        System.out.println(taskManager.getEpics());
    }
}
