package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.impl.FileBackedTasksManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super(url);
        client = new KVTaskClient(url);
        gson = Managers.getGson();

    }

    //void put(String key, String json) POJO to Json -> toJson(Object obj)
    //переопределенный save() — сериализуем и отправляем через KV client на сервер вместо записи в файл
    @Override
    public void save() {
        String task = gson.toJson(tasks.values());
        String epic = gson.toJson(epics.values());
        String subTask = gson.toJson(subTasks.values());
        String history = gson.toJson(historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList()));

        client.put("tasks", task);
        client.put("epics", epic);
        client.put("subTasks", subTask);
        client.put("history", history);

    }

    //String load(String key) String Json to POJO -> fromJson(String json, Class<T> clasёs)
    public void loadFromKVServer() {
        //сначала выгружаем через KVTaskClient задачи
        String taskJson = client.load("tasks");
        String epicJson = client.load("epics");
        String subTaskJson = client.load("subTasks");
        String history = client.load("history");

        //Десериализуем из Gson через TypeToken<T> и получаем правильный параметризованный тип
        List<Task> tasks = gson.fromJson((taskJson), new TypeToken<ArrayList<Task>>() {}.getType());
        if (tasks != null) {
            tasks.forEach(this::addNewTask);
        }
        List<Epic> epics = gson.fromJson((epicJson), new TypeToken<ArrayList<Epic>>() {}.getType());
        if (epics != null) {
            epics.forEach(this::addNewEpic);
        }
        List<SubTask> subTasks = gson.fromJson((subTaskJson), new TypeToken<ArrayList<SubTask>>() {}.getType());
        if (subTasks != null) {
            subTasks.forEach(this::addNewSubTask);
        }
        List<Integer> historyMemory = gson.fromJson((history), new TypeToken<ArrayList<Integer>>() {}.getType());
        for (Integer id : historyMemory) {
            if (this.tasks.containsKey(id)) {
                getTaskId(id);
            } else if (this.epics.containsKey(id)) {
                getEpicId(id);
            } else if (this.subTasks.containsKey(id)) {
                getSubTaskId(id);
            }
        }

    }

}
