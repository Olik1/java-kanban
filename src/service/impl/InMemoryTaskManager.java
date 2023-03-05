package service.impl;

import exeptions.ManagerCrossingException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> priorityTasks = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime() == null && o2.getStartTime() == null)
                return 0;
            if (o1.getStartTime() == null && o2.getStartTime() != null)
                return 1;
            if (o1.getStartTime() != null && o2.getStartTime() == null)
                return -1;

            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    }); //хранение задач по приоритету

    public List<Task> getPrioritizedTasks() { //возвращает список задач и подзадач в заданном порядке
        return new ArrayList<>(priorityTasks);
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

    private void updateEpicStart(Epic epic) {
        //пройтись по всем подзадачам и взять подзадачу у кот.старт наименьший
        LocalDateTime minStart = null;
        for (Integer idSubtask : epic.getSubTasks()) {
            SubTask subTask = subTasks.get(idSubtask);
            if (subTask.getStartTime() != null) {
                //сравниваем в min ничего не записано или время старта < min, тогда присваиваем старттайм
                if (minStart == null || subTask.getStartTime().isBefore(minStart)) {
                    minStart = subTask.getStartTime();
                }
            }
        }
        epic.setStartTime(minStart);
    }

    private void updateEpicEnd(Epic epic) {
        LocalDateTime maxEnd = null;
        for (Integer idSubtask : epic.getSubTasks()) {
            SubTask subTask = subTasks.get(idSubtask);
            if (subTask.getEndTime() != null) {
                if (maxEnd == null || subTask.getEndTime().isAfter(maxEnd)) {
                    maxEnd = subTask.getEndTime();
                }
            }
        }
        epic.setEndTime(maxEnd);
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("Список задач пуст!");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.size() == 0) {
            System.out.println("Список эпиков пуст!");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        if (subTasks.size() == 0) {
            System.out.println("Список сабтасков пуст!");
            return Collections.emptyList();
        }
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        subTasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            setStatusForEpic(epic);
        }
    }

    @Override
    public Task getTaskId(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addTask(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Epic getEpicId(int id) {
        if (epics.containsKey(id)) {
            historyManager.addTask(epics.get(id));
            return epics.get(id);
        }
        return null;
    }

    @Override
    public SubTask getSubTaskId(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.addTask(subTasks.get(id));
            return subTasks.get(id);
        }
        return null;
    }

    private void setStatusForEpic(Epic epic) {
        if (epic != null) {
            epic.setStatus(Status.NEW);
            boolean isCheckValue = false;
            for (Integer subId : epic.getSubTasks()) {
                SubTask subtask = subTasks.get(subId);
                if (isCheckValue == false) {
                    epic.setStatus(subtask.getStatus());
                    isCheckValue = true;
                }
                if (epic.getStatus() != subtask.getStatus()) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
            updateEpicDuration(epic);
            updateEpicStart(epic);
            updateEpicEnd(epic);
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
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTasks().add(subTask.getId());
        setStatusForEpic(epic);
        addPrioritizedTasks(subTask);

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
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subTask : epic.getSubTasks()) {
                subTasks.remove(subTask);
                historyManager.remove(subTask);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            epic.getSubTasks().remove((Integer) subTask.getId());
            subTasks.remove(id);
            historyManager.remove(id);
            setStatusForEpic(epic);
        }
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

