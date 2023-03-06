
import http.HttpTaskManager;
import http.KVServer;
import org.junit.jupiter.api.*;

public class HttpTaskManagerTest extends TaskManagerTest {

    private KVServer server;

    @BeforeEach
    void beforeEach()  {
        server = new KVServer();
        server.start();

        manager = new HttpTaskManager("http://localhost:" + KVServer.PORT);
    }

    @AfterEach
    void afterEach() {
        server.stop();
    }

}