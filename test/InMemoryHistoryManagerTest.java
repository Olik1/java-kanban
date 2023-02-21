import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.impl.InMemoryHistoryManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    protected Task createNewTask() {
        return new Task("Task", "Description of Task", Status.NEW);
    }

    LocalDateTime createDate(int y, int m, int d, int h, int min) {
        return LocalDateTime.of(y, m, d, h, min, 0);
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @DisplayName("Проверка на добавление в историю")
    @Test
    public void addTasksToHistoryTest() {
        Task task1 = createNewTask();
        historyManager.addTask(task1);
        List<Task> list = historyManager.getHistory();
        assertNotNull(historyManager, "История не пустая.");
        assertEquals(1, list.size(), "История не пустая.");
        ;
    }

    @DisplayName("Добавление дубликата в историю")
    @Test
    public void addDublicateTasktoHistoryTest() {
        Task task = createNewTask();
        historyManager.addTask(task);
        historyManager.addTask(task);
        List<Task> list = historyManager.getHistory();
        assertEquals(1, list.size());
    }

    @DisplayName("Проверка на удаление из историю")
    @Test
    public void removeTaskTest() {
        Task task = createNewTask();
        historyManager.addTask(task);
        historyManager.remove(task.getId());
        assertEquals(Collections.emptyList(), historyManager.getHistory(), "История не удалена");
    }

    @DisplayName("Удаление элементов из истории")
    @Test
    public void removeTaskFromHeadOfHistoryTest() {
        Task task1 = new Task("Task1", Status.NEW, "Description", 30,
                createDate(2023, 2, 19, 15, 23));
        Task task2 = new Task("Task2", Status.NEW, "Description", 45,
                createDate(2023, 2, 19, 9, 40));
        Task task3 = new Task("Task3", Status.NEW, "Description", 15,
                createDate(2023, 2, 19, 4, 10));
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.remove(task1.getId());
        List<Task> list = historyManager.getHistory();
        assertEquals(task2, list.get(0));
        assertEquals(task3, list.get(1));
        assertEquals(2, list.size());
    }
    @Test
    public void removeTaskFromTheMiddleOfHistoryTest() {
        Task task1 = new Task("Task1", Status.NEW, "Description", 30,
                createDate(2023, 2, 19, 15, 23));
        Task task2 = new Task("Task2", Status.NEW, "Description", 45,
                createDate(2023, 2, 19, 9, 40));
        Task task3 = new Task("Task3", Status.NEW, "Description", 15,
                createDate(2023, 2, 19, 4, 10));
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.remove(task2.getId());
        List<Task> list = historyManager.getHistory();
        assertEquals(task1, list.get(0));
        assertEquals(task3, list.get(1));
        assertEquals(2, list.size());
    }
    @Test
    public void removeTaskFromTailOfHistoryTest() {
        Task task1 = new Task("Task1", Status.NEW, "Description", 30,
                createDate(2023, 2, 19, 15, 23));
        Task task2 = new Task("Task2", Status.NEW, "Description", 45,
                createDate(2023, 2, 19, 9, 40));
        Task task3 = new Task("Task3", Status.NEW, "Description", 15,
                createDate(2023, 2, 19, 4, 10));
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.remove(task3.getId());
        List<Task> list = historyManager.getHistory();
        assertEquals(task1, list.get(0));
        assertEquals(task2, list.get(1));
        assertEquals(2, list.size());
    }
    @DisplayName("Пустая история")
    @Test
    public void shouldReturnEmptyHistory() {
        List<Task> list = historyManager.getHistory();
        assertTrue(list.isEmpty());
    }
}