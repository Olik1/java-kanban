package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.Handlers.TaskHandler;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class HttpTaskServer {
    private final int PORT = 8080;
    private TaskManager taskManager;
    private HttpServer httpServer;
    private Gson gson;


    public HttpTaskServer() {
        this.taskManager = Managers.getInMemoryDefault();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT),0);
        //создаем эндпоинты
        httpServer.createContext("/tasks/task", new TaskHandler(taskManager));
        gson = Managers.getGson();
        /*
        API должен работать так, чтобы все запросы по пути /tasks/<ресурсы> приходили в интерфейс TaskManager.
        Путь для обычных задач — /tasks/task, для подзадач — /tasks/subtask, для эпиков — /tasks/epic.
        Получить все задачи сразу можно будет по пути /tasks/, а получить историю задач по пути /tasks/history.
         */

    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        httpTaskServer.stop();
    }

    public void start() {
        httpServer.start();
        System.out.println("Начало работы сервера на порту: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);//int delay - the maximum time in seconds to wait until exchanges have finished.
        System.out.println("Прекращение работы сервера на порту: " + PORT);
    }
}
