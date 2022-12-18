package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager implements TaskManager {
    int newId;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private int increaseId() {
        return ++newId;
    }

    //Получение списка всех задач:
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    //Удаление всех задач. - clear:
    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            setStatusForEpic(epic);
        }
    }

    //Получение по идентификатору:
    @Override
    public Task getTaskId(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicId(int id) {
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskId(int id) {
        return subTasks.get(id);
    }

    //обновление статуса
    private void setStatusForEpic(Epic epic) {
/*    1) просто присваиваем NEW в надежде, что мы не пойдем ни в какой цикл и ничего не поменяется.
      2) на самой первой итерации присваиваем эпику значение первого сабтаска.
      Тогда если в ходе обхода сабтасков новых статусов не обнаружится, то в эпике будет правильный статус всех сабтасков
      3) если в ходе обхода мы обнаружим разницу между статусом, который присвоен эпику (из первого сабтаска)
      и текущим обрабатываемым сабтаском, тогда можно сразу поставить IN_PROGRESS и выходить из цикла
 */
        epic.setStatus(Status.NEW);
        boolean check = false;
        for (Integer subId : epic.getSubTasks()) {
            SubTask subtask = getSubTaskId(subId);
            if (check == false) {
                //присваиваем первому сабтаску из эпика значение текущего сабтаска в цикле
                epic.setStatus(subtask.getStatus());
                check = true; // меняем флаг чтобы больше статус первого сабтаска больше не присваивался
            } //если у нас есть разница то все сабтаски в процессе и выходим из метода
            if (epic.getStatus() != subtask.getStatus()) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
    }

    //Создание. Сам объект должен передаваться в качестве параметра:
    @Override
    public void addNewTask(Task task) {
        task.setId(increaseId());
        task.setStatus(Status.NEW);
        tasks.put(newId, task);

    }

    @Override
    public void addNewEpic(Epic epic) {
        epic.setId(increaseId());
        epics.put(newId, epic);
        setStatusForEpic(epic);
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        /*не забываем подзадачи связаны с эпиком
        на вход подаем объект SubTask, проверка на наличие ключа чтобы избежать null
        вносим айди в список родительского эпика
        потом загружаем ее в общий список задач и список подзадач.
        Обновляем статус родителя
        если задача не найдена - пишем пользователю - задача не найдена
        */
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }
        subTask.setId(increaseId());
        subTasks.put(newId, subTask);
        Epic epic = getEpicId(subTask.getEpicId());
        epic.getSubTasks().add(newId);
        setStatusForEpic(epic);
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не существует!");
        }

    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            setStatusForEpic(epic);
        } else {
            System.out.println("Эпик не существует!");
        }
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        //проверка на null
        if (subTasks.containsKey(subTask.getId())) {
            Epic epic = epics.get(subTask.getEpicId());
            subTasks.put(subTask.getId(), subTask);
            setStatusForEpic(epic);
        } else {
            System.out.println("Подзадача не существует!");
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(Integer id) {
        epics.remove(id);
        //добавить метод по удалению сабтасков
    }

    @Override
    public void deleteSubTask(Integer id) {
        subTasks.remove(id);
    }

    @Override
    public List<Task> getAllSubtasks(Epic epic) {
        ArrayList<Task> spisok = new ArrayList<>();
        for (Integer id : epic.getSubTasks()) {
            SubTask subTask = getSubTaskId(id);
            spisok.add(subTask);
        }
        return spisok;
    }

}
