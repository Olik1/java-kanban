package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EpicHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = Managers.getGson();

    public EpicHandler(TaskManager taskManager) {
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
                    String response = gson.toJson(taskManager.getAllEpics());
                    System.out.println("GET EPICS: " + response);
                    sendText(httpExchange, response);
                } else {
                    String pathId = query.replaceFirst("id=", "");
                    int id = parsePathId(pathId);
                    if (id != -1) {
                        String response = gson.toJson(taskManager.getEpicId(id));
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
                    Epic epic = gson.fromJson(request, Epic.class);
                    int id = epic.getId();
                    if (taskManager.getEpicId(id) != null) {
                        taskManager.updateEpic(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                        System.out.println("Успешное обновление эпика по id " + id);
                    } else {
                        taskManager.addNewEpic(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                        int epicId = epic.getId();
                        System.out.println("Эпик успешно добавлена по айди" + epicId);
                    }
                    if (epic.getSubTasks() == null) {
                        epic.setSubTasks(new ArrayList<>());
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
                    taskManager.clearAllEpics();
                    httpExchange.sendResponseHeaders(200, 0);
                    System.out.println("Удаление всех эпиков прошло успешно");
                } else {
                    String pathId = query.replaceFirst("id=", "");
                    int id = parsePathId(pathId);
                    if (id != -1) {
                        taskManager.deleteEpicById(id);
                        System.out.println("Эпик с id " + id + " успешно удален!");
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