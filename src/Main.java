import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача №1", "Выполнить", Status.NEW);
        Task task2 = new Task("Задача №1", "Выполнить", Status.NEW);
        System.out.println("Добавляем задачки");
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.getTaskId(task2.getId());
        taskManager.getTaskId(task1.getId());
        System.out.println(taskManager.getTaskId(task1.getId()));
        System.out.println(taskManager.getTaskId(task2.getId())); //запросите созданные задачи несколько раз в разном порядке
        System.out.println("Печатаем историю: " + taskManager.getHistory()); //
        taskManager.deleteTaskById(2);
        System.out.println("После удаления задачи: " + taskManager.getAllTasks());
        Epic epic1 = new Epic("Эпик №1", "Обязательно выполнить");
        SubTask subTask1 = new SubTask("Подзадача №1",
                "Выполнение не обязательно", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача №2",
                "Выполнение не обязательно", Status.NEW, 3);
        SubTask subTask3 = new SubTask("Подзадача №3",
                "Выполнение не обязательно", Status.NEW, 3);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        taskManager.addNewSubTask(subTask3);
        taskManager.getEpicId(epic1.getId());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasksByEpic(epic1));
        taskManager.clearAllEpics();
        System.out.println("Список эпиков после удаления:" + taskManager.getAllEpics());
        System.out.println("Список сабтасков после удаления:" + taskManager.getAllSubtasks());
        System.out.println("История просмотров после удаления:" + "\n" + taskManager.getHistory());

    }
}
