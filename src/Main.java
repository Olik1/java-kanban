import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task ("Задача №1", "Выполнить", Status.NEW);
        Task task2 = new Task("Задача №3", "Выполнить", Status.NEW);
        Epic epic1 = new Epic("Эпик №1", "Обязательно выполнить");
        Epic epic2 = new Epic("Эпик №2", "Обязательно выполнить");
        SubTask subTask1 = new SubTask("Подзадача №1",
                "Выполнение не обязательно",Status.NEW, 1);
        SubTask subTask2 = new SubTask("Подзадача №2",
                "Выполнение не обязательно",Status.NEW, 1);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        System.out.println(taskManager.getHistory());
    }
}
