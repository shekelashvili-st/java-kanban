package manager.historymanager;

import manager.tasks.Epic;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> idToNode;
    private Node listHead = null;
    private Node listTail = null;

    public InMemoryHistoryManager() {
        idToNode = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Integer taskId = task.getId();
        Node duplicate = idToNode.get(taskId);

        if (duplicate != null) {
            removeNode(duplicate);
        }
        linkLast(task);
        idToNode.put(taskId, listTail);
    }

    @Override
    public void remove(int id) {
        Node node = idToNode.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> guardedHistory = new ArrayList<>();
        for (Node currentNode = listHead; currentNode != null; currentNode = currentNode.getNext()) {
            Task task = currentNode.getValue();
            if (task instanceof Subtask subtask) {
                guardedHistory.add(new Subtask(subtask));
            } else if (task instanceof Epic epic) {
                guardedHistory.add(new Epic(epic));
            } else {
                guardedHistory.add(new Task(task));
            }
        }
        return guardedHistory;
    }

    private void linkLast(Task task) {
        Node taskNode;

        if (listHead == null) {
            taskNode = new Node(task, null, null);
            listHead = taskNode;
        } else {
            taskNode = new Node(task, null, listTail);
            listTail.setNext(taskNode);
        }
        listTail = taskNode;
    }

    private void removeNode(Node node) {
        Node previous = node.getPrevious();
        Node next = node.getNext();

        if (previous == null) {
            listHead = next;
        } else {
            previous.setNext(next);
        }
        if (next == null) {
            listTail = previous;
        } else {
            next.setPrevious(previous);
        }
    }

    public static class Node {
        private Task value;
        private Node next;
        private Node previous;

        public Node(Task value, Node next, Node previous) {
            this.value = value;
            this.next = next;
            this.previous = previous;
        }

        public Task getValue() {
            return value;
        }

        public void setValue(Task value) {
            this.value = value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }
    }
}
