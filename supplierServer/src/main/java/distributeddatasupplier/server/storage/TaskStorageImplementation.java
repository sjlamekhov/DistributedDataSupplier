package distributeddatasupplier.server.storage;

import distributeddatasupplier.server.persistence.TaskPersistenceLayer;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

import java.util.*;

public class TaskStorageImplementation implements TaskStorage {

    private final TaskPersistenceLayer taskPersistenceLayer;

    public TaskStorageImplementation(TaskPersistenceLayer taskPersistenceLayer) {
        this.taskPersistenceLayer = taskPersistenceLayer;
    }

    @Override
    public Task getAnyTask() {
        Collection<Task> tasks = taskPersistenceLayer.getTasksByStatus(TaskStatus.NOT_STARTED, 1);
        if (tasks.isEmpty()) {
            return Task.EMPTY_TASK;
        } else {
            Task task = tasks.iterator().next();
            taskPersistenceLayer.setTaskStatus(task.getUri(), TaskStatus.IN_PROCESSING);
            return task;
        }
    }

    @Override
    public Task getTaskById(String taskId) {
        return taskPersistenceLayer.getByUri(new TaskUri(taskId));
    }

    @Override
    public void addTask(Task task) {
        if (!Objects.equals(task, Task.EMPTY_TASK)) {
            taskPersistenceLayer.add(task);
        }
    }

    @Override
    public void deleteTaskById(String taskId) {
        taskPersistenceLayer.deleteObject(new TaskUri(taskId));
    }

    @Override
    public boolean isEmpty(TaskStatus taskStatus) {
        return !taskPersistenceLayer.hasTasksWithStatus(taskStatus);
    }

    @Override
    public void markTaskAsFinished(String taskId) {
        taskPersistenceLayer.setTaskStatus(new TaskUri(taskId), TaskStatus.FINISHED);
    }
}
