package manager.historymanager;

import manager.historymanager.datastructure.Node;
import manager.tasks.Epic;
import manager.tasks.Subtask;
import manager.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> idToNode;
    private Node<Task> listHead = null;
    private Node<Task> listTail = null;
    private List<Task> cachedHistory = null;

    public InMemoryHistoryManager() {
        idToNode = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Integer taskId = task.getId();
        Node<Task> duplicate = idToNode.get(taskId);

        if (duplicate != null) {
            removeNode(duplicate);
        }
        linkLast(task);
        idToNode.put(taskId, listTail);
        cachedHistory = null;
    }

    @Override
    public void remove(int id) {
        Node<Task> node = idToNode.remove(id);
        if (node != null) {
            removeNode(node);
        }
        cachedHistory = null;
    }

    @Override
    public List<Task> getHistory() {
        if (cachedHistory == null) {
            updateCachedHistory();
        }
        List<Task> guardedHistory = new ArrayList<>(cachedHistory.size());
        for (Task task : cachedHistory) {
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
        Node<Task> taskNode;

        if (listHead == null) {
            taskNode = new Node<>(task, null, null);
            listHead = taskNode;
        } else {
            taskNode = new Node<>(task, null, listTail);
            listTail.setNext(taskNode);
        }
        listTail = taskNode;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> previous = node.getPrevious();
        Node<Task> next = node.getNext();

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

    private void updateCachedHistory() {
        cachedHistory = new ArrayList<>();
        for (Node<Task> currentNode = listHead; currentNode != null; currentNode = currentNode.getNext()) {
            cachedHistory.add(currentNode.getValue());
        }
    }
}
