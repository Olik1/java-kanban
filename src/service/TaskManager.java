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
    Epic getEpicId(int id);
    SubTask getSubTaskId(int id);

    //Создание. Сам объект должен передаваться в качестве параметра:
    void addNewTask(Task task);
    void addNewEpic(Epic epic);
    void addNewSubTask(SubTask subTask);

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(SubTask subTask);
    //Удаление по идентификатору. - remove:
    void deleteTaskById(Integer id);
    void deleteEpic(Integer id);
    void deleteSubTask(Integer id);

    //Получение списка всех подзадач определённого эпика:
    List<Task> getAllSubtasks(Epic epic);

}
