package http;

import service.impl.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super(url);
        client = new KVTaskClient(url);

    }
}
