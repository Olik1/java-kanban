package service.impl;

import model.*;
import service.HistoryManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public static void main(String[] args) {

    }

    private final File file; //свойство в кот.хранится путь к файлу бэкапа
    private static final String CSV_PATH = "id,type,name,status,description,epic\n";

    // 1,TASK,Task1,NEW,Description task1,
    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public String toString(Task task) {
        return String.format("%s,%s,%s,%s,%s,%s\n",
                task.getId(), task.getClass().getSimpleName().toUpperCase(),
                task.getName(), task.getStatus(), task.getDescription(), null);
    }

    public void save() { //сохраняет текущее состояние менеджера в указанный файл.
        try (Writer writer = new FileWriter(file)) {
            writer.write(CSV_PATH);
            for (Task task : tasks.values()) {
                writer.write(toString(task));
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic));
            }
            for (SubTask subTask : subTasks.values()) {
                writer.write(toString(subTask));
            }
            writer.write("\n");

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }

    }

    public static Task fromString(String value) {
        int id;
        String name;
        Status status;
        String description;
        String[] parts = value.split(",");
        id = Integer.parseInt(parts[0]);
        name = parts[2];
        status = Status.valueOf(parts[3]);
        description = parts[4];
        int epicId = Integer.parseInt(parts[5]);

        switch (TaskType.valueOf(parts[1])) {
            case TASK:
                return new Task(id, name, status, description);
            case EPIC:
                return new Epic(id, name, status, description, new ArrayList<>());
            case SUBTASK:
                return new SubTask(id, name, status, description, epicId);
            default: return null;
        }
    }
    static String historyToString(HistoryManager manager) { //сохраняет историю в CSV
        StringBuilder builder = new StringBuilder();
        if (manager.getHistory().size() != 0) {
            for (Task task : manager.getHistory()) {
                builder.append(task.getId()).append(",");
            }
        }
        return builder.toString();
    }
    //возвращает список ID задач истории
    //восстанавливает менеджер истории из CSV, сохранять будем в loadFromFile
    static List<Integer> historyFromString(String value) { //
        List<Integer> list = new ArrayList<>();
        String[] parts = value.split(",");
        for (String line : parts) {
            list.add(Integer.valueOf(line));
        }
        return list;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        //save()
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        //save()
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        //save()
    }

    @Override
    public Task getTaskId(int id) {
        Task task = super.getTaskId(id);
        //save()
        return task;
    }

    @Override
    public Epic getEpicId(int id) {
        Epic epic = super.getEpicId(id);
        //save()
        return epic;
    }

    @Override
    public SubTask getSubTaskId(int id) {
        SubTask subTask = super.getSubTaskId(id);
        //save()
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        //save()
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        //save()
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        //save()
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        //save()
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
        //save()
    }

    @Override
    public void deleteSubTask(Integer id) {
        super.deleteSubTask(id);
        //save()
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        //save()
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        //save()
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        //save()
    }

}
