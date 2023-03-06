package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskManager;
import http.KVServer;
import http.LocalDateTimeAdapter;
import service.impl.FileBackedTasksManager;
import service.impl.InMemoryHistoryManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getTaskManagerDefault() {
        return FileBackedTasksManager.loadFromFile(new File("test_tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getHttpDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:" + KVServer.PORT);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.setPrettyPrinting().create();
    }
}
