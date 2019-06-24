package distributeddatasupplier.server.persistence;

import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

import java.util.*;

public class InMemoryTaskPersistence implements TaskPersistenceLayer {

    private final Map<TaskUri, Task> tasks;

    public InMemoryTaskPersistence() {
        tasks = new HashMap<>();
    }

    @Override
    public boolean isUriPresented(TaskUri uri) {
        return tasks.containsKey(uri);
    }

    @Override
    public Task add(Task object) {
        Objects.requireNonNull(object);
        tasks.put(object.getUri(), object);
        return object;
    }

    @Override
    public Task update(Task object) {
        Objects.requireNonNull(object);
        tasks.put(object.getUri(), object);
        return object;
    }

    @Override
    public Task getByUri(TaskUri objectUri) {
        return tasks.get(objectUri);
    }

    @Override
    public Collection<Task> getByUris(Collection<TaskUri> objectUris) {
        List<Task> result = new ArrayList<>();
        for (TaskUri taskUri : objectUris) {
            Task task = tasks.get(taskUri);
            if (task != null) {
                result.add(task);
            }
        }
        return result;
    }

    @Override
    public void deleteObject(TaskUri uri) {
        tasks.remove(uri);
    }

    @Override
    public Collection<Task> getTasksByStatus(TaskStatus taskStatus, int limit) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getTaskStatus() == taskStatus) {
                result.add(task);
            }
            if (result.size() >= limit) {
                break;
            }
        }
        return result;
    }

    @Override
    public void setTaskStatus(TaskUri taskUri, TaskStatus taskStatus) {
        Task task = tasks.get(taskUri);
        if (task != null) {
            task.setTaskStatus(taskStatus);
            tasks.put(taskUri, task);
        }
    }

    @Override
    public boolean hasTasksWithStatus(TaskStatus taskStatus) {
        for (Task task : tasks.values()) {
            if (task.getTaskStatus() == taskStatus) {
                return true;
            }
        }
        return false;
    }
}
