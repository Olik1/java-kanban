import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManagerImpl;

public class Main {

    public static void main(String[] args) {
        Task task = new Task("Задача 1", "медведь 1", Status.NEW);
        TaskManagerImpl manager = new TaskManagerImpl();
        manager.addNewTask(task);
        System.out.println(manager.getTaskId(1));
        //manager.clearAllTasks();
        //System.out.println(manager.getAllTasks());
        manager.deleteTaskById(0);
        //Task task1 = new Task("Задача 1.1", "Эпик 1", Status.NEW);
        //manager.addNewTask(task1);
        manager.updateTask(new Task("Задача 1", "медведь1.2", Status.DONE));
        System.out.println(manager.getTaskId(1));
        //System.out.println(manager.getAllTasks());

        System.out.println(manager.getEpicId(1));
        manager.clearAllEpics();
        manager.deleteEpic(1);
        Epic epic = new Epic("Эпик1", "побороть медведя");
        Epic epic1 = new Epic("ТЗ-3", "надо доделать, нельзя тянуть");
        manager.addNewEpic(epic);
        manager.addNewEpic(epic1);
        SubTask subTask = new SubTask("Подзадача 1.1.", "Обязательно выполнить",
                Status.NEW, 1);
        SubTask subTask2 = new SubTask("Подзадача 1.1.", "Обязательно выполнить",
                Status.NEW, 1);
        SubTask subTask3 = new SubTask("Подзадача 1.2.", "Обязательно выполнить",
                Status.NEW, 2);
        manager.addNewSubTask(subTask);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);
        System.out.println("До удаления эпика 1: " + manager.getEpicId(1));
        System.out.println(manager.getAllSubtasksByEpic(epic));
        System.out.println(manager.getSubTaskId(3));
        manager.updateEpic(epic);
        System.out.println(manager.getSubTaskId(2));
        System.out.println(manager.getAllSubtasks());
        manager.deleteEpic(1);
        System.out.println("После удаления эпика 1: " + manager.getEpicId(1));
        System.out.println("После удаления эпика 1 сабтаски: " + manager.getAllSubtasksByEpic(epic));
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasksByEpic(epic1));

    }
}
