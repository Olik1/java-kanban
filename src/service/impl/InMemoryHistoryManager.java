package service.impl;

import model.Task;
import service.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> memoryMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public List<Task> getTasks() {
        List<Task> listOfTask = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) { //пока нода не пустая
            listOfTask.add(node.data); //добавление данных этого узла в список
            node = node.next; //Следующий узел
        }
        return listOfTask;
    }

    @Override
    public void addTask(Task task) {
        if (memoryMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        addLast(task);
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
        //public Node(E data, Node<E> next, Node<E> prev)
        final Node newNode = new Node(task, null, oldTail);
        tail = newNode;
        memoryMap.put(task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        memoryMap.put(task.getId(), newNode);
    }


    public void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            memoryMap.remove(node.data.getId());
            node.data = null;
            if (head == node && tail == node) {
                //если нода пустая = null - то ничего вообще не делаем
                head = null;
                tail = null;
            } else if (head == node) {
                //если нода = нода1 (она же голова), то теперь голова = нода2,
                // ссылка на предыдущий элемент, у ноды2 удаляем, потому что она ссылалась на голову
                head = next;
                head.prev = null;
            } else if (tail == node) {
                //та же схема с хвостом
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }

    }

    static class Node<E> {
        private E data;
        private Node<E> next;
        private Node<E> prev;

        public Node(E data, Node<E> next, Node<E> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

    }
}
