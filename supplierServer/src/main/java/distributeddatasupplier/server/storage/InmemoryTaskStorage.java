package distributeddatasupplier.server.storage;

import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

import java.util.*;

public class InmemoryTaskStorage implements TaskStorage {

    private final Map<String, Task> tasks;

    public InmemoryTaskStorage() {
        this.tasks = new HashMap<>();
    }

    @Override
    public Task getAnyTask() {
        if (!tasks.isEmpty()) {
            Task task = tasks.values().stream()
                    .filter(t -> t.getTaskStatus() == TaskStatus.NOT_STARTED)
                    .findAny().orElse(Task.EMPTY_TASK);
            if (!Objects.equals(task, Task.EMPTY_TASK)) {
                tasks.get(task.getUri().getId()).setTaskStatus(TaskStatus.IN_PROCESSING);
            }
            return task;
        } else {
            return Task.EMPTY_TASK;
        }
    }

    @Override
    public Task getTaskById(String taskId) {
        return tasks.getOrDefault(taskId, null);
    }

    @Override
    public void addTask(Task task) {
        if (!Objects.equals(task, Task.EMPTY_TASK)) {
            tasks.put(task.getUri().getId(), task);
        }
    }

    @Override
    public void deleteTaskById(String taskId) {
        tasks.remove(taskId);
    }

    @Override
    public boolean isEmpty() {
        return tasks.values().stream().noneMatch(t -> t.getTaskStatus() == TaskStatus.NOT_STARTED);
    }

    @Override
    public void markTaskAsFinished(String taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.get(taskId).setTaskStatus(TaskStatus.FINISHED);
        }
    }
}
