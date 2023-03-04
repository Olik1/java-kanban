package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SubtaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson = Managers.getGson();

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    private int parsePathId(String strPath) {
        try {
            return Integer.parseInt(strPath);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();//для определения действия сервера
        String path = httpExchange.getRequestURI().getPath();//из запроса вытягиваем URI и получаем путь
        String query = httpExchange.getRequestURI().getQuery(); //для получения строки запроса после ?

        System.out.println("Идет обработка запроса " + path + " с помощью метода " + method);

        switch (method) {
            case "GET": {
                if (query == null) {
                    String response = gson.toJson(taskManager.getAllSubtasks());
                    System.out.println("GET SUBTASKS: " + response);
                    sendText(httpExchange, response);
                } else {
                    String pathId = query.replaceFirst("id=", "");
                    int id = parsePathId(pathId);
                    if (id != -1) {
                        String response = gson.toJson(taskManager.getSubTaskId(id));
                        sendText(httpExchange, response);
                    } else {
                        System.out.println("Введен некорректный id" + pathId);
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }
                break;
            }
            case "POST": {
                String request = readText(httpExchange);
                try {
                    SubTask subTask = gson.fromJson(request, SubTask.class);
                    int id = subTask.getId();
                    if (taskManager.getSubTaskId(id) != null) {
                        taskManager.updateSubtask(subTask);
                        httpExchange.sendResponseHeaders(200, 0);
                        System.out.println("Успешное обновление подзадачи по id " + id);
                    } else {
                        taskManager.addNewSubTask(subTask);
                        httpExchange.sendResponseHeaders(200, 0);
                        int subTaskId = subTask.getId();
                        System.out.println("Подзадача успешно добавлена по айди" + subTaskId);
                    }
                } catch (JsonSyntaxException ex) {
                    httpExchange.sendResponseHeaders(400,0);
                    System.out.println("Введен неправильный формат запроса");
                }
                break;
            }
            case "DELETE": {
                if (query == null) {
                    // если у нас нет уточнения по айди в строке запроса, то удаляем все
                    taskManager.clearAllSubtasks();
                    httpExchange.sendResponseHeaders(200, 0);
                    System.out.println("Удаление всех подзадач прошло успешно");
                } else {
                    String pathId = query.replaceFirst("id=", "");
                    int id = parsePathId(pathId);
                    if (id != -1) {
                        taskManager.deleteSubTaskById(id);
                        System.out.println("Задача с id " + id + " успешно удалена!");
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        System.out.println("Введен некорректный id" + pathId);
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }
                break;
            }

            default: {
                httpExchange.sendResponseHeaders(405, 0);
                System.out.println("Ожидали GET или DELETE запрос, но получен - " + method);
            }
        }

        httpExchange.close();
    }

    protected String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
    }
}