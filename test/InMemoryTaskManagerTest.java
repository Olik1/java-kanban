import service.TaskManager;
import service.impl.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    public InMemoryTaskManagerTest() {
        manager = new InMemoryTaskManager();
    }
}
