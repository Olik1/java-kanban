package http.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.Managers;
import service.TaskManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;


public class HistoryHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson = Managers.getGson();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();//для определения действия сервера
        String path = httpExchange.getRequestURI().getPath();//из запроса вытягиваем URI и получаем путь
        String query = httpExchange.getRequestURI().getQuery(); //для получения строки запроса после ?

        System.out.println("Идет обработка запроса " + path + " с помощью метода " + method);

        if ("GET".equals(method)) {
            if (query == null) {
                String response = gson.toJson(taskManager.getHistory());
                System.out.println("GET History: " + response);
                sendText(httpExchange, response);
            }
        } else {
            httpExchange.sendResponseHeaders(405, 0);
            System.out.println("Ожидали GET  запрос, но получен - " + method);
        }

        httpExchange.close();
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
    }
}