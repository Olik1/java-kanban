import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.impl.FileBackedTasksManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileTaskManagerTest extends TaskManagerTest {
    static final String FILE_NAME = "test_tasks.csv";

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(new File(FILE_NAME));
    }

    @DisplayName("Проверка загрузки файла и сравнение истории")
    @Test
    public void loadFromFileTest() {
        Task task1 = new Task("Task1", Status.NEW, "Description", 40, createDate(2023, 2, 19, 10, 0));
        Task task2 = new Task("Task2", "Description", Status.NEW);
        Task task3 = new Task("Task3", Status.NEW, "Description", 25, createDate(2023, 2, 19, 18, 0));
        Task task4 = new Task("Task4", Status.NEW, "Description", 70, createDate(2023, 2, 19, 7, 0));

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);
        manager.addNewTask(task4);
        manager.getTaskId(task1.getId());

        FileBackedTasksManager resultManager = FileBackedTasksManager.loadFromFile(new File(FILE_NAME));
        assertEquals(4, resultManager.getAllTasks().size());
        assertEquals(manager.getAllTasks(), resultManager.getAllTasks());
        assertEquals(manager.getHistory(), resultManager.getHistory());
    }

    @DisplayName("Проверка загрузки файла и сравнение истории с добавлением сабтасков")
    @Test
    public void loadFromFileEpicTest() {
        Epic epic1 = new Epic("Эпик №1", "Обязательно выполнить");
        SubTask subTask1 = new SubTask("Подзадача №1", Status.NEW, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача №2", Status.NEW, "Пойти",
                20, createDate(2023, 2, 19, 20, 0), epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача №3", Status.NEW, "Пойти",
                50, createDate(2023, 2, 19, 12, 0), epic1.getId());
        SubTask subTask4 = new SubTask("Подзадача №3", "Пойти", Status.NEW, epic1.getId());

        manager.addNewEpic(epic1);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);
        manager.addNewSubTask(subTask4);

        FileBackedTasksManager resultManager = FileBackedTasksManager.loadFromFile(new File(FILE_NAME));
        assertEquals(manager.getAllEpics(), resultManager.getAllEpics());
        assertEquals(manager.getAllSubtasks(), resultManager.getAllSubtasks());
        assertEquals(0, resultManager.getHistory().size());
    }

    @DisplayName("Проверка на получение пустого списка истории")
    @Test
    public void loadEmptyFileTest() {
        Task task1 = new Task("Task1", Status.NEW, "Description", 40,
                createDate(2023, 2, 19, 10, 0));
        Epic epic = new Epic("Эпик №1", "Обязательно выполнить");
        SubTask subTask = new SubTask("Подзадача №1", Status.NEW, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic.getId());
        manager.addNewTask(task1);
        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask);
        manager.clearAllTasks();
        manager.clearAllEpics();
        manager.clearAllSubtasks();

        FileBackedTasksManager resultManager = FileBackedTasksManager.loadFromFile(new File(FILE_NAME));
        assertEquals(0, resultManager.getAllTasks().size());
        assertEquals(0, resultManager.getAllEpics().size());
        assertEquals(0, resultManager.getAllSubtasks().size());
        assertEquals(0, resultManager.getHistory().size());
    }
}
