import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected Task createNewTask() {
        return new Task("Task", "Description of Task", Status.NEW);
    }
    protected Epic createNewEpic() {
        return new Epic("Epic", "Description of Epic");
    }
    protected SubTask createNewSubtask(Epic epic) {
        return new SubTask("Subtask", "Description of Subtask", Status.NEW, epic.getId());
    }

    @DisplayName("Проверка добавление задач")
    @Test
    public void addNewTaskTest() {
        Task task = createNewTask();
        manager.addNewTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());



    }
}
