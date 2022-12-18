import model.Epic;
import model.Status;
import model.SubTask;
import service.Manager;

public class Main {

    public static void main(String[] args) {
//        Task task = new Task("Задача 1", "медведь 1", Status.NEW);
        Manager manager = new Manager();
//        manager.addNewTask(task);
//        System.out.println(manager.getTaskId(1));
//        //manager.clearAllTasks();
//        //System.out.println(manager.getAllTasks());
//        manager.deleteTaskById(0);
//        //Task task1 = new Task("Задача 1.1", "Эпик 1", Status.NEW);
//        //manager.addNewTask(task1);
//        manager.updateTask(new Task("Задача 1", "медведь1.2", Status.DONE));
//        System.out.println(manager.getTaskId(1));
//        //System.out.println(manager.getAllTasks());
        Epic epic = new Epic("Эпик1", "побороть медведя");
        manager.addNewEpic(epic);
//        System.out.println(manager.getEpicId(1));
//        manager.clearAllEpics();
//        manager.deleteEpic(1);
        System.out.println(manager.getAllEpics());
        SubTask subTask = new SubTask("Подзадача 1.1.", "Обязательно выполнить", Status.NEW, 1);
        manager.addNewSubTask(subTask);





    }
}
/*
2.1 Получение списка всех задач - task(done), epic(done), subtask()
2.2 Удаление всех задач. - task(done), epic(done), subtask(not done, если удаляется эпик удаляем и сабтаски)
2.3 Получение по идентификатору. - task(done), epic(done), subtask()
2.4 Создание. - task(done), epic(done), subtask()
2.5 Обновление. - task(done), epic(), subtask()
2.6 Удаление по идентификатору. - task(done), epic(done), subtask()
3.1 Получение списка всех подзадач определённого эпика. - task(), epic(), subtask()
4.1
4.2

 */