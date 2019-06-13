package distributeddatasupplier.server.storage;

import objects.Task;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public class InmemoryTaskStorage implements TaskStorage {

    private final Queue<Task> tasks;

    public InmemoryTaskStorage() {
        this.tasks = new ArrayDeque<>();
    }

    @Override
    public Task getAnyTask() {
        if (!tasks.isEmpty()) {
            return tasks.poll();
        } else {
            return Task.EMPTY_TASK;
        }
    }

    @Override
    public Task getTaskById(String taskId) {
        return tasks.stream().filter(i -> taskId.equals(i.getUri().getId())).findAny().orElse(null);
    }

    @Override
    public void addTask(Task task) {
        if (!Objects.equals(task, Task.EMPTY_TASK)) {
            tasks.add(task);
        }
    }

    @Override
    public void deleteTaskById(String taskId) {
        tasks.removeIf(i -> taskId.equals(i.getUri().getId()));
    }

    @Override
    public boolean isEmpty() {
        return tasks.isEmpty();
    }
}
