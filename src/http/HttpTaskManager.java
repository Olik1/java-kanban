package http;

import service.impl.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    public HttpTaskManager(String url) throws IOException, InterruptedException {
        //super(new File(url));
        super(null);
        client = new KVTaskClient(url);



    }
}
