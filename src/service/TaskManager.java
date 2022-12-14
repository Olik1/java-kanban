package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    //Получение списка всех задач:
    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<SubTask> getAllSubtasks();

    //Удаление всех задач. - clear:
    void clearAllTasks();
    void clearAllEpics();
    void clearAllSubtasks();

    //Получение по идентификатору:
    Task getTaskId(int id);
    Epic getEpicId();
    SubTask getEpicId(int id);

    //Создание. Сам объект должен передаваться в качестве параметра:
    void createNewTask(Task task); // или int?
    void createNewEpic(Epic epic);
    void createNewSubTask(SubTask subTask);

    //Создание. Сам объект должен передаваться в качестве параметра:
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(SubTask subTask);
    //Удаление по идентификатору. - remove:
    void deleteTaskById(int id);
    void deleteEpic(int id);
    void deleteSubTask(int id);

    //Получение списка всех подзадач определённого эпика:
    List<Task> getAllSubtasks(Epic epic);

}
