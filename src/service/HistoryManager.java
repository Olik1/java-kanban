package service;

import model.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addTask(Task task);

}
