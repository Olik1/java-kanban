package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskManager;
import http.LocalDateTimeAdater;
import service.impl.FileBackedTasksManager;
import service.impl.InMemoryHistoryManager;
import service.impl.InMemoryTaskManager;

import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getInMemoryDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static TaskManager getHttpDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdater());
        return gsonBuilder.create();
    }
}
