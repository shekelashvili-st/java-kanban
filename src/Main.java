public class Main {

    public static void main(String[] args) {
        var taskManager = new TaskManager();
        var task1 = new Task(null, "Сделать что-то одно", "Не портить ничего", Status.NEW);
        var task2 = new Task(null, "Сделать что-то второе", "Не портить ничего", Status.IN_PROGRESS);

        // Test on empty lists
        System.out.println("Начало выполнения программы:");
        System.out.println(taskManager.getTasks());
        taskManager.deleteTasks();

        // Add new tasks and test cloning
        Task task1IdCopy = taskManager.createTask(task1); // Because of clone we can't break anything with set methods
        task1IdCopy.setName("А давайте что-нибудь другое");
        Task task2IdCopy = taskManager.createTask(task2);
        task2IdCopy.setStatus(Status.DONE);
        System.out.println("Добавлены две задачи:");
        System.out.println(taskManager.getTasks());
        assert (taskManager.getTasks().get(1) != task1IdCopy &&
                taskManager.getTasks().get(1) != taskManager.getTaskById(1));

        // Test deleting and modifying tasks
        Task task1Deleted = taskManager.deleteTaskById(1);
        taskManager.updateTask(task2IdCopy);
        System.out.println("Удалена первая задача и изменена вторая:");
        System.out.println(taskManager.getTasks());
        taskManager.deleteTasks();
        System.out.println("Удалены все задачи:");
        System.out.println(taskManager.getTasks());
    }
}
