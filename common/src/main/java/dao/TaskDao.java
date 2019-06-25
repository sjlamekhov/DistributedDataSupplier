package dao;

import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;
import persistence.tasks.TaskPersistenceLayer;

import java.util.Collection;

public class TaskDao {

    private final TaskPersistenceLayer persistenceLayer;

    public TaskDao(TaskPersistenceLayer taskPersistenceLayer) {
        this.persistenceLayer = taskPersistenceLayer;
    }

    public boolean isUriPresented(TaskUri uri) {
        return persistenceLayer.isUriPresented(uri);
    }

    public Task add(Task object) {
        return persistenceLayer.add(object);
    }

    public Task update(Task object) {
        return persistenceLayer.update(object);
    }

    public Task getByUri(TaskUri objectUri) {
        return persistenceLayer.getByUri(objectUri);
    }

    public Collection<Task> getByUris(Collection<TaskUri> objectUris) {
        return persistenceLayer.getByUris(objectUris);
    }

    public void deleteObject(TaskUri uri) {
        persistenceLayer.deleteObject(uri);
    }

    public Collection<Task> getTasksByStatus(TaskStatus taskStatus, int limit) {
        return persistenceLayer.getTasksByStatus(taskStatus, limit);
    }

    public void setTaskStatus(TaskUri taskUri, TaskStatus taskStatus) {
        persistenceLayer.setTaskStatus(taskUri, taskStatus);
    }

    public boolean hasTasksWithStatus(TaskStatus taskStatus) {
        return persistenceLayer.hasTasksWithStatus(taskStatus);
    }
}
