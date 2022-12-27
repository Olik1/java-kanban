package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int SIZE = 10;
    List<Task> taskList = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return taskList;
    }

    @Override
    public void addTasks(Task task) {
        if (taskList.size() == SIZE) {
            taskList.remove(0);
        } else {
            taskList.add(task);
        }
    }

}
