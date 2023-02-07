package service.impl;

import exeptions.ManagerLoadException;
import exeptions.ManagerSaveException;
import model.*;
import service.HistoryManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final String CSV_PATH = "report.csv";
    private final File file; //свойство в кот.хранится путь к файлу бэкапа

    public static void main(String[] args) {

        File fileForExample = new File(CSV_PATH);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(fileForExample);
        System.out.println(fileBackedTasksManager.getAllTasks());
        System.out.println(fileBackedTasksManager.getAllEpics());
        System.out.println(fileBackedTasksManager.getAllSubtasks());
        System.out.println(listToNiceString(fileBackedTasksManager.getHistory()));

        Task task = new Task("Olga", "funny", Status.NEW);
        fileBackedTasksManager.addNewTask(task);
        Epic epic1 = new Epic("Приготовить хавчик", "сделать вкусно");
        Epic epic2 = new Epic("Помыть кота", "Погладить кота");
        fileBackedTasksManager.addNewEpic(epic1);
        fileBackedTasksManager.addNewEpic(epic2);
        SubTask subTask1 = new SubTask("Подзадача №3",
                "побрить кота", Status.NEW, epic1.getId());
        fileBackedTasksManager.addNewSubTask(subTask1);
        fileBackedTasksManager.getTaskId(task.getId());

        fileBackedTasksManager.getSubTaskId(subTask1.getId());
        fileBackedTasksManager.getEpicId(epic1.getId());
        fileBackedTasksManager.getTaskId(task.getId());


    }


    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public String toString(Task task) {
        return String.format("%s,%s,%s,%s,%s,%s\n",
                task.getId(), task.getClass().getSimpleName().toUpperCase(),
                task.getName(), task.getStatus(), task.getDescription(),
                task.getClass().getSimpleName().equals("SubTask") ?
                        Integer.toString(((SubTask) task).getEpicId()) : "");
    }

    public void save() { //сохраняет текущее состояние менеджера в указанный файл.
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
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
            writer.write(historyToString(historyManager));

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }

    //создает экземпляр менеджера на основе файла, считывает файл построчно и восстанавливает структуру заданий
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        // Фйла может не быть при первом запуске программы.
        if (!file.exists()) return fileBackedTasksManager;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Первую строку читаем и игнорируем. Это названия столбцов.
            reader.readLine();

            Boolean timeToParseHistory = false;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    timeToParseHistory = true;
                } else if (!timeToParseHistory) {
                    Task task = fromString(line);

                    if (task != null) {
                        if (task.getClass().getSimpleName().toUpperCase().equals("TASK")) {
                            fileBackedTasksManager.addNewTask(task);
                        } else if (task.getClass().getSimpleName().toUpperCase().equals("EPIC")) {
                            Epic newEpic = (Epic) task;
                            fileBackedTasksManager.addNewEpic(newEpic);
                        } else if (task.getClass().getSimpleName().toUpperCase().equals("SUBTASK")) {
                            SubTask newSubTask = (SubTask) task;
                            fileBackedTasksManager.addNewSubTask(newSubTask);
                        }
                    }
                } else {
                    List<Integer> listOfId = historyFromString(line);
                    if (!listOfId.isEmpty()) {
                        for (Integer integer : listOfId) {
                            fileBackedTasksManager.getTaskId(integer);
                            fileBackedTasksManager.getEpicId(integer);
                            fileBackedTasksManager.getSubTaskId(integer);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
        return fileBackedTasksManager;
    }

    public static Task fromString(String value) {

        int id;
        String name;
        Status status;
        String description;
        String[] parts = value.split(",");
        TaskType taskType;
        if (parts.length >= 5) {
            id = Integer.parseInt(parts[0]);
            name = parts[2];
            status = Status.valueOf(parts[3]);
            description = parts[4];
            taskType = TaskType.valueOf(parts[1]);

            switch (taskType) {
                case TASK:
                    return new Task(id, name, status, description);
                case EPIC:
                    return new Epic(id, name, status, description, new ArrayList<>());
                case SUBTASK:
                    int epicId = Integer.parseInt(parts[5]);
                    return new SubTask(id, name, status, description, epicId);
                default:
                    return null;
            }
        }
        return null;
    }


    private static String historyToString(HistoryManager manager) { //сохраняет историю в CSV
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
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public Task getTaskId(int id) {
        Task task = super.getTaskId(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicId(int id) {
        Epic epic = super.getEpicId(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskId(int id) {
        SubTask subTask = super.getSubTaskId(id);
        save();
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(Integer id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        save();
    }

    private static <E> String listToNiceString(List<E> list) //шаблонный метод для читаемого вывода
    {
        String ret = "\n[\n";
        for (E task : list) {
            ret += "    ";
            ret += task;
            ret += "\n";
        }
        ret += "]";
        return ret;
    }

}