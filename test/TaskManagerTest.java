import exeptions.ManagerCrossingException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest {
    protected TaskManager manager;

    protected Task createNewTask() {
        return new Task("Task", "Description of Task", Status.NEW);
    }

    protected Epic createNewEpic() {
        return new Epic("Эпик №1", "Обязательно выполнить");
    }

    protected SubTask createNewSubtask(Epic epic) {
        return new SubTask("Подзадача №1", "Обязательно выполнить", Status.NEW, epic.getId());
    }

    LocalDateTime createDate(int y, int m, int d, int h, int min) {
        return LocalDateTime.of(y, m, d, h, min, 0);
    }

    @DisplayName("Проверка добавление задач")
    @Test
    public void addNewTaskTest() {
        Task task = createNewTask();
        manager.addNewTask(task);
        List<Task> tasks = manager.getAllTasks();

        assertEquals(Status.NEW, task.getStatus());
        assertEquals(1, tasks.size());
    }

    @Test
    public void addNewEpicTest() {
        Epic epic = createNewEpic();
        manager.addNewEpic(epic);
        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
    }

    @Test
    public void addNewSubtaskTest() {
        Epic epic = createNewEpic();
        SubTask subTask1 = createNewSubtask(epic);
        SubTask subTask2 = createNewSubtask(epic);
        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        List<SubTask> subTasks = manager.getAllSubtasks();
        assertNotNull(subTasks.size());
        assertNotNull(subTask1.getStatus());
        assertEquals(Status.NEW, subTask1.getStatus());
        assertEquals(epic.getId(), subTask1.getEpicId());
        assertEquals(List.of(subTask1, subTask2), subTasks);

    }

    @Test
    public void addGetPrioritizedTasks() {
        Task task1 = new Task("Task", Status.NEW, "Description", 40, createDate(2023, 2, 19, 10, 0));
        Task task2 = new Task("Task", "Description", Status.NEW);
        Task task3 = new Task("Task", Status.NEW, "Description", 40, createDate(2023, 2, 19, 18, 0));
        Task task4 = new Task("Task", Status.NEW, "Description", 40, createDate(2023, 2, 19, 7, 0));

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);
        manager.addNewTask(task4);


        List<Task> tasks = manager.getPrioritizedTasks();
        List<Task> expected = new ArrayList<>(List.of(task4, task1, task3, task2));


        assertEquals(expected, tasks);
    }

    @DisplayName("Проверка начала и окончания времени эпика по сабтаскам")
    @Test
    public void durationStartEndEpicTest() {
        LocalDateTime minDate = createDate(2023, 2, 19, 7, 0);
        LocalDateTime maxDate = createDate(2023, 2, 19, 20, 0);
        LocalDateTime expectedDateEnd = maxDate.plusMinutes(20);

        Epic epic = createNewEpic();
        SubTask subTask1 = new SubTask("Подзадача №1", Status.NEW, "Пойти",
                30, minDate, epic.getId());
        SubTask subTask2 = new SubTask("Подзадача №2", Status.NEW, "Пойти",
                20, maxDate, epic.getId());
        SubTask subTask3 = new SubTask("Подзадача №3", Status.NEW, "Пойти",
                50, createDate(2023, 2, 19, 12, 0), epic.getId());
        SubTask subTask4 = new SubTask("Подзадача №3", "Пойти", Status.NEW, epic.getId());

        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);
        manager.addNewSubTask(subTask4);

        long expected = 100;
        long actual = epic.getDuration();

        assertEquals(expected, actual);
        assertEquals(minDate, epic.getStartTime());
        assertEquals(expectedDateEnd, epic.getEndTime());
    }

    @DisplayName("Проверка пересечения времени")
    @Test
    public void crossTimeTest() {
        Task task1 = new Task("Task", Status.NEW, "Description", 40, createDate(2023, 2, 19, 10, 0));
        Task task2 = new Task("Task", "Description", Status.NEW);
        Task task3 = new Task("Task", Status.NEW, "Description", 40, createDate(2023, 2, 19, 18, 0));
        Task task4 = new Task("Task", Status.NEW, "Description", 40, createDate(2023, 2, 19, 9, 40));

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        ManagerCrossingException exception = assertThrows(ManagerCrossingException.class, () -> {
            manager.addNewTask(task4);
        });

        assertEquals("Задачи пересекаются!", exception.getMessage());
    }

    @DisplayName("Проверка статуса задач")
    @Test
    public void checkStatusNewEpic() {
        Epic epic = createNewEpic();
        SubTask subTask1 = new SubTask("Подзадача №1", Status.NEW, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic.getId());
        SubTask subTask2 = new SubTask("Подзадача №2", Status.NEW, "Пойти",
                20, createDate(2023, 2, 19, 20, 0), epic.getId());
        SubTask subTask3 = new SubTask("Подзадача №3", Status.NEW, "Пойти",
                50, createDate(2023, 2, 19, 12, 0), epic.getId());

        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void checkStatusDoneEpic() {
        Epic epic = createNewEpic();
        SubTask subTask1 = new SubTask("Подзадача №1", Status.DONE, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic.getId());
        SubTask subTask2 = new SubTask("Подзадача №2", Status.DONE, "Пойти",
                20, createDate(2023, 2, 19, 20, 0), epic.getId());
        SubTask subTask3 = new SubTask("Подзадача №3", Status.DONE, "Пойти",
                50, createDate(2023, 2, 19, 12, 0), epic.getId());

        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void checkStatusProgressEpic() {
        Epic epic = createNewEpic();
        SubTask subTask1 = new SubTask("Подзадача №1", Status.NEW, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic.getId());
        SubTask subTask2 = new SubTask("Подзадача №2", Status.DONE, "Пойти",
                20, createDate(2023, 2, 19, 20, 0), epic.getId());
        SubTask subTask3 = new SubTask("Подзадача №3", Status.DONE, "Пойти",
                50, createDate(2023, 2, 19, 12, 0), epic.getId());

        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkStatusNoSubtasksEpic() {
        Epic epic = createNewEpic();
        manager.addNewEpic(epic);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void statusProgressChangeDoneEpic() {
        Epic epic = createNewEpic();
        SubTask subTask1 = new SubTask("Подзадача №1", Status.NEW, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic.getId());
        SubTask subTask2 = new SubTask("Подзадача №2", Status.DONE, "Пойти",
                20, createDate(2023, 2, 19, 20, 0), epic.getId());
        SubTask subTask3 = new SubTask("Подзадача №3", Status.DONE, "Пойти",
                50, createDate(2023, 2, 19, 12, 0), epic.getId());

        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);
        manager.deleteSubTaskById(subTask1.getId());

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void checkStatusSubtask() {
        Epic epic = createNewEpic();
        manager.addNewEpic(epic);
        SubTask subTask = new SubTask("Подзадача №1", Status.NEW, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic.getId());
        manager.addNewSubTask(subTask);
        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subTask);
        assertEquals(Status.IN_PROGRESS, subTask.getStatus());
        subTask.setStatus(Status.DONE);
        manager.updateSubtask(subTask);
        assertEquals(Status.DONE, subTask.getStatus());

    }

    @Test
    public void checkStatusTaskDone() {
        Task task = createNewTask();
        manager.addNewTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, task.getStatus());
    }

    @DisplayName("Удаление эпика")
    @Test
    public void removeEpicTest() {
        Epic epic1 = new Epic("Эпик №1", "Обязательно выполнить");
        SubTask subTask1 = new SubTask("Подзадача №1", Status.NEW, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача №2", Status.DONE, "Пойти",
                20, createDate(2023, 2, 19, 20, 0), epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача №3", Status.DONE, "Пойти",
                50, createDate(2023, 2, 19, 12, 0), epic1.getId());

        Epic epic2 = new Epic("Эпик №2", "Обязательно выполнить");
        SubTask subTask4 = new SubTask("Подзадача №4", Status.NEW, "Пойти",
                30, createDate(2023, 2, 19, 7, 0), epic2.getId());

        manager.addNewEpic(epic1);
        manager.addNewSubTask(subTask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);
        manager.addNewEpic(epic2);
        manager.addNewSubTask(subTask4);
        manager.deleteEpicById(epic1.getId());

        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @DisplayName("Удаление по айди")
    @Test
    public void deleteTaskByIdTest() {
        Task task = createNewTask();
        manager.addNewTask(task);
        manager.deleteTaskById(task.getId());
        assertEquals(Collections.emptyList(), manager.getAllTasks());
    }

    @DisplayName("Проверка что удаляются также и сабтаски")
    @Test
    public void deleteEpicByIdTest() {
        Epic epic = createNewEpic();
        SubTask subTask = createNewSubtask(epic);

        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask);
        manager.deleteEpicById(epic.getId());

        assertEquals(Collections.emptyList(), manager.getAllEpics());
        assertEquals(Collections.emptyList(), manager.getAllSubtasks());
    }

    @DisplayName("Получение задач из пустого списка задач")
    @Test
    public void getEmptyTaskTest() {
        Task task = createNewTask();
        assertNull(manager.getTaskId(task.getId()));
        assertTrue(manager.getAllTasks().isEmpty()); //дабл чек ради теста с булевым ассертом
    }

    @Test
    public void getEmptyEpicTest() {
        Epic epic = createNewEpic();
        assertNull(manager.getEpicId(epic.getId()));
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void getEmptySubtaskTest() {
        Epic epic = createNewEpic();
        SubTask subTask1 = createNewSubtask(epic);
        manager.addNewEpic(epic);
        assertNull(manager.getSubTaskId(subTask1.getId()));
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @DisplayName("Попытка получения задач с несуществующим id")
    @Test
    public void getWrongIdTest() {
        assertNull(manager.getTaskId(13));
        assertNull(manager.getEpicId(14));
        assertNull(manager.getSubTaskId(15));
    }

    @DisplayName("Удаление всех задач")
    @Test
    public void clearAllTasksEpicsSubtasks() {
        Task task = createNewTask();
        Epic epic = createNewEpic();
        SubTask subTask = createNewSubtask(epic);
        manager.addNewTask(task);
        manager.addNewEpic(epic);
        manager.addNewSubTask(subTask);
        manager.clearAllTasks();
        manager.clearAllEpics();
        manager.clearAllSubtasks();
        assertEquals(Collections.emptyList(), manager.getAllTasks());
        assertEquals(Collections.emptyList(), manager.getAllEpics());
        assertEquals(Collections.emptyList(), manager.getAllSubtasks());
    }


}
