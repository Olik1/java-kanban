package http;

import service.impl.FileBackedTasksManager;

import java.io.File;

public class HttpTaskManager extends FileBackedTasksManager {
    public HttpTaskManager(String url) {
        super(new File(url)); // исправить заглушку на // super(url);


    }
}
