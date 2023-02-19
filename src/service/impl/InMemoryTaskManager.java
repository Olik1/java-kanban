package service.impl;

import exeptions.ManagerCrossingException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> priorityTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime)); //хранение задач по приоритету

    private List<Task> getPrioritizedTasks() { //возвращает список задач и подзадач в заданном порядке
        return priorityTasks.stream().collect(Collectors.toList());
    }

    private void addPrioritizedTasks(Task task) {
        priorityTasks.add(task);
    }

    private boolean isAnyCrossingTask(Task task) {
        if (getPrioritizedTasks().isEmpty()) {
            return false;
        }
        if (task.getStartTime() == null) {
            return false;
        }
        for (Task priorityTask : priorityTasks) {
            if (priorityTask.getStartTime() == null || priorityTask.getEndTime() == null) {
                continue;
            }

            if (task.getStartTime().isAfter(priorityTask.getStartTime()) &&
                    task.getStartTime().isBefore(priorityTask.getEndTime()) ||
                    task.getEndTime().isAfter(priorityTask.getStartTime()) &&
                            task.getEndTime().isBefore(priorityTask.getEndTime()) ||
                    priorityTask.getStartTime().isAfter(task.getStartTime()) &&
                            priorityTask.getStartTime().isBefore(task.getEndTime()) ||
                    priorityTask.getEndTime().isAfter(task.getStartTime()) &&
                            priorityTask.getEndTime().isBefore(task.getEndTime())
            ) {
                return true;
            }
        }
        return false;
    }
    private void updateEpicDuration(Epic epic) {//добавляем в сабтаск
        long duration = 0L;
        for (Integer idSubTask : epic.getSubTasks()) {
            SubTask subTask = subTasks.get(idSubTask);
            duration += subTask.getDuration();
        }
        epic.setDuration(duration);
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

    @Override
    public void clearAllTasks() {
        for (Task task : tasks.values())
            historyManager.remove(task.getId());
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        for (Epic epic : epics.values())
            historyManager.remove(epic.getId());
        epics.clear();

        for (SubTask subTask : subTasks.values())
            historyManager.remove(subTask.getId());
        subTasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        for (SubTask subTask : subTasks.values())
            historyManager.remove(subTask.getId());
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
        if (isAnyCrossingTask(task)) {
            throw new ManagerCrossingException();
        }
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
        addPrioritizedTasks(task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        setStatusForEpic(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        if (isAnyCrossingTask(subTask)) {
            throw new ManagerCrossingException();
        }
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        Epic epic = getEpicId(subTask.getEpicId());
        epic.getSubTasks().add(subTask.getId());
        setStatusForEpic(epic);
        addPrioritizedTasks(subTask);
        updateEpicDuration(epic);
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
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(Integer id) {
        Epic epic = epics.get(id);
        for (Integer subTask : epic.getSubTasks()) {
            subTasks.remove(subTask);
            historyManager.remove(subTask);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(Integer id) {
        Epic epic = epics.get(id);
        SubTask subTask = subTasks.get(id);
        epic.getSubTasks().remove(subTask.getId());
        setStatusForEpic(epic);
        subTasks.remove(id);
        historyManager.remove(id);
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

