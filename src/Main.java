import http.KVServer;
import http.KVTaskClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        KVTaskClient client = new KVTaskClient("http://localhost:8078");
        //Обращается к KVServer, чтобы сохранять и получать данные не в файле, а на сервере
        client.put("tasks", "[{\"id\":18, \"title\": \"Task\", \"description\":\"Task description\"}]");
        client.put("100500", "Посадить собаку на стул");
        System.out.println(client.load("tasks"));
        System.out.println(client.load("100500"));
        kvServer.stop();

    }
}
