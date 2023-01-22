import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.util.List;

public class Main {
    public static<E> String listToNiceString(List<E> list) //шаблонный метод для читаемого вывода
    {
        String ret = "\n[\n";
        for(E task : list)
        {
            ret += "    ";
            ret += task;
            ret += "\n";
        }
        ret += "]";
        return ret;
    }

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Одна задача",   "Замесить", Status.NEW);
        Task task2 = new Task("Другая задача", "Нарубить", Status.NEW);

        System.out.println("Добавляем задачки:");
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        taskManager.getTaskId(task2.getId());
        taskManager.getTaskId(task1.getId());

        System.out.println(taskManager.getTaskId(task1.getId()));
        System.out.println(taskManager.getTaskId(task2.getId())); //запросите созданные задачи несколько раз в разном порядке
        System.out.println();

        System.out.println("Печатаем историю просмотров: " + listToNiceString(taskManager.getHistory())); //
        System.out.println();

        taskManager.deleteTaskById(task2.getId());
        System.out.println("Список задач после удаления второй задачи: " + listToNiceString(taskManager.getAllTasks()));
        System.out.println();

        Epic epic1 = new Epic("Эпик №1", "Обязательно выполнить");
        SubTask subTask1 = new SubTask("Подзадача №1",
                "Пойти", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача №2",
                "Туда", Status.NEW, epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача №3",
                "Не знаю", Status.NEW, epic1.getId());
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        taskManager.addNewSubTask(subTask3);
        taskManager.getEpicId(epic1.getId());

        System.out.println("Список всех эпиков" + listToNiceString(taskManager.getAllEpics()));
        System.out.println();


        System.out.println("Список всех подзадач певого эпика epic1" + listToNiceString(taskManager.getAllSubtasksByEpic(epic1)));
        System.out.println();

        // taskManager.clearAllEpics();
        taskManager.deleteEpic(epic1.getId());

        System.out.println("История просмотров:" + listToNiceString(taskManager.getHistory()));
        System.out.println();

        System.out.println("Список всех эпиков после удаления:" + listToNiceString(taskManager.getAllEpics()));
        System.out.println();

        System.out.println("Список всех сабтасков после удаления:" + listToNiceString(taskManager.getAllSubtasks()));
        System.out.println();

        System.out.println("История просмотров после удаления:" + listToNiceString(taskManager.getHistory()));
        System.out.println();
    }
}
