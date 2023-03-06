package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.Handlers.*;
import service.Managers;
import service.TaskManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class HttpTaskServer {
    private final static int PORT = 8080;
    private TaskManager taskManager;
    private HttpServer httpServer;
    private Gson gson;


    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getTaskManagerDefault();

        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT),0);
        //создаем эндпоинты
        httpServer.createContext("/tasks/task", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/epic", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/history", new HistoryHandler(taskManager));
        httpServer.createContext("/tasks", new PrioritizedTaskHandler(taskManager));

        gson = Managers.getGson();
    }

    public static void main(String[] args) throws IOException {
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