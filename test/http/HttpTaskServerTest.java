package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static Gson gson;
    private static HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private static final String TASK_PATH = "http://localhost:8080/tasks/task";
    private static final String EPIC_PATH = "http://localhost:8080/tasks/epic";
    private static final String SUBTASK_PATH = "http://localhost:8080/tasks/subtask";
    private static final String HISTORY_PATH = "http://localhost:8080/tasks/history";
    Task task1;
    Task task2;
    Epic epic1;
    Epic epic2;
    SubTask subTask1;
    SubTask subTask2;

    LocalDateTime createDate(int year, int month, int day, int hours, int minutes) {
        return LocalDateTime.of(year, month, day, hours, minutes, 0);
    }

    private void createTasks() {
        LocalDateTime time = createDate(2022, 3, 3, 23, 31);
        task1 = new Task("Дописать тест", Status.NEW, "Проверить перед отправкой", 30,
                time);
        task2 = new Task("Запустить тест", Status.NEW, "Проверить результат", 30,
                time.plusHours(2));
        epic1 = new Epic("Обрести свой путь в жизни", "Обязательно выполнить!");
        epic2 = new Epic("Купить дом", "С видом на море");
        subTask1 = new SubTask("Подзадача - пройти обучение", Status.NEW, "закончить спринт",
                10, time.plusHours(3), epic2.getId());
        subTask2 = new SubTask("Подзадача - проверить знания", Status.NEW, "повторить конспект",
                10, time.plusHours(3), epic2.getId());
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
    }

    @BeforeAll
    static void start() throws IOException {
        gson = Managers.getGson();
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        taskManager = Managers.getHttpDefault();
        httpTaskServer = new HttpTaskServer();
        createTasks();
        httpTaskServer.start();
    }

    @AfterEach
    public void tearDown() {
        httpTaskServer.stop();
    }

    @AfterAll
    static void stop() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void postTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_PATH);
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        HttpRequest.BodyPublisher bodyJson1 = HttpRequest.BodyPublishers.ofString(taskJson1);
        HttpRequest.BodyPublisher bodyJson2 = HttpRequest.BodyPublishers.ofString(taskJson2);
        HttpRequest request1 = HttpRequest.newBuilder().POST(bodyJson1).uri(uri).build();
        HttpRequest request2 = HttpRequest.newBuilder().POST(bodyJson2).uri(uri).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        assertEquals(200, response2.statusCode());

    }

    @Test
    void getTaskResponseTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_PATH);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNotNull(response.body());

    }

    @Test
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_PATH + "?id=1");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
    @Test
    void deleteTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(TASK_PATH);
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }


    @Test
    void postEpicTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_PATH);
        String epicJson1 = gson.toJson(epic2);
        HttpRequest.BodyPublisher bodyJson1 = HttpRequest.BodyPublishers.ofString(epicJson1);
        HttpRequest request1 = HttpRequest.newBuilder().POST(bodyJson1).uri(uri).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
    }
    @Test
    void deleteEpicByIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(EPIC_PATH + "?id=3");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
    @Test
    void postSubtaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(SUBTASK_PATH);
        String epicJson = gson.toJson(subTask1);
        HttpRequest.BodyPublisher bodyJson1 = HttpRequest.BodyPublishers.ofString(epicJson);
        HttpRequest request1 = HttpRequest.newBuilder().POST(bodyJson1).uri(uri).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        System.out.println(subTask1.getEpicId());
    }
    @Test
    void deleteSubtaskByIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(SUBTASK_PATH + "?id=4");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(HISTORY_PATH);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNotNull(gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType()));
    }

}