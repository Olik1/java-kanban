package service.impl;

import model.Task;
import service.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> memoryMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public List<Task> getTasks() {
        List<Task> listOfTask = new ArrayList<>();
        Node node = head;
        while (node != null) {
            listOfTask.add(node.data); //добавление данных этого узла в список
            node = node.next; //Следующий узел
        }
        return listOfTask;
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            remove(task.getId());
            addLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (memoryMap.containsKey(id)) {
            removeNode(memoryMap.get(id));
            memoryMap.remove(id);
        }
    }


    public void addLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(task, null, oldTail);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        memoryMap.put(task.getId(), newNode);
    }


    public void removeNode(Node node) {
        if (node != null) {
            final Node next = node.next;
            final Node prev = node.prev;
            memoryMap.remove(node.data.getId());
            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node) {
                head = next;
                head.prev = null;
            } else if (tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }

    static class Node {
        private Task data;
        private Node next;
        private Node prev;

        public Node(Task data, Node next, Node prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

    }
}