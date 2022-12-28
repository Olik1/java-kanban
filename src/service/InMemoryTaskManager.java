package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    int nextId;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    private int increaseId() {
        return ++nextId;
    }

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

    @Override
    public Task getTaskId(int id) {
        historyManager.addTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicId(int id) {
        historyManager.addTask(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskId(int id) {
        historyManager.addTask(subTasks.get(id));
        return subTasks.get(id);
    }

    private void setStatusForEpic(Epic epic) {
        epic.setStatus(Status.NEW);
        boolean isCheckValue = false;
        for (Integer subId : epic.getSubTasks()) {
            SubTask subtask = getSubTaskId(subId);
            if (isCheckValue == false) {
                epic.setStatus(subtask.getStatus());
                isCheckValue = true;
            }
            if (epic.getStatus() != subtask.getStatus()) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
    }

    @Override
    public void addNewTask(Task task) {
        task.setId(increaseId());
        task.setStatus(Status.NEW);
        tasks.put(nextId, task);

    }

    @Override
    public void addNewEpic(Epic epic) {
        epic.setId(increaseId());
        setStatusForEpic(epic);
        epics.put(nextId, epic);
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }
        subTask.setId(increaseId());
        subTasks.put(nextId, subTask);
        Epic epic = getEpicId(subTask.getEpicId());
        epic.getSubTasks().add(nextId);
        setStatusForEpic(epic);
    }

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
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    @Override
    public void deleteEpic(Integer id) {
        Epic epic = epics.get(id);
        for (Integer subTask : epic.getSubTasks()) {
            subTasks.remove(subTask);

        }
        epics.remove(id);
    }

    @Override
    public void deleteSubTask(Integer id) {
        Epic epic = epics.get(id);
        SubTask subTask = subTasks.get(id);
        epic.getSubTasks().remove(subTask.getId());
        setStatusForEpic(epic);
        subTasks.remove(id);

    }

    @Override
    public List<Task> getAllSubtasksByEpic(Epic epic) {
        ArrayList<Task> spisok = new ArrayList<>();
        for (Integer id : epic.getSubTasks()) {
            SubTask subTask = getSubTaskId(id);
            spisok.add(subTask);
        }
        return spisok;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
