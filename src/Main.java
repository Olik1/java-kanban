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
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.getTaskId(task1.getId());
        taskManager.getTaskId(task1.getId());
        Epic epic1 = new Epic("Эпик №1", "Обязательно выполнить");
        Epic epic2 = new Epic("Эпик №2", "Обязательно выполнить");
        SubTask subTask1 = new SubTask("Подзадача №1",
                "Выполнение не обязательно",Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача №2",
                "Выполнение не обязательно",Status.NEW, epic2.getId());
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        taskManager.getEpicId(epic1.getId());
        System.out.println(taskManager.getHistory());
    }
}
