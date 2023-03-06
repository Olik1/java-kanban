package http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import service.Managers;
import service.impl.FileBackedTasksManager;

import java.io.IOException;
import java.lang.reflect.GenericSignatureFormatError;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {

    private HttpTaskManager httpTaskManager;
    private KVServer server;
    private Gson gson;



    @BeforeEach
    void beforeEach()  {
        try {
            server = new KVServer();
            server.start();
           // httpTaskManager = new HttpTaskManager();
            gson = Managers.getGson();
        } catch (IOException e) {
            System.out.println("Сервер не создался, засада");
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }

    }

    @AfterEach
    void afterEach() {
        server.stop();
    }

    @Test
    void save() {

    }

    @Test
    void loadFromFile() {
    }
}