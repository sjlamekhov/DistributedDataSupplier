package persistence.tasks;

import com.mongodb.BasicDBObject;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;
import persistence.converters.ObjectConverter;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskPersistence implements TaskPersistenceLayer {

    private final Map<TaskUri, BasicDBObject> tasks;
    private final ObjectConverter<Task, BasicDBObject> converter;

    public InMemoryTaskPersistence(ObjectConverter<Task, BasicDBObject> converter) {
        this.tasks = new HashMap<>();
        this.converter = converter;
    }

    @Override
    public boolean isUriPresented(TaskUri uri) {
        return tasks.containsKey(uri);
    }

    @Override
    public Task add(Task object) {
        Objects.requireNonNull(object);
        BasicDBObject dbObject = converter.buildToFromObject(object);
        tasks.put(object.getUri(), dbObject);
        return object;
    }

    @Override
    public Task update(Task object) {
        Objects.requireNonNull(object);
        BasicDBObject dbObject = converter.buildToFromObject(object);
        tasks.put(object.getUri(), dbObject);
        return object;
    }

    @Override
    public Task getByUri(TaskUri taskUri) {
        BasicDBObject dbObject = tasks.get(taskUri);
        if (dbObject == null) {
            return null;
        }
        return converter.buildObjectFromTO(dbObject);
    }

    @Override
    public Collection<Task> getByUris(Collection<TaskUri> objectUris) {
        List<Task> result = new ArrayList<>();
        for (TaskUri taskUri : objectUris) {
            Task task = getByUri(taskUri);
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
    public Collection<TaskUri> getObjectUris(int responseSizeLimit) {
        return tasks.keySet().stream().limit(responseSizeLimit)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Task> getTasksByStatus(TaskStatus taskStatus, int limit) {
        List<Task> result = new ArrayList<>();
        for (BasicDBObject dbObject : tasks.values()) {
            Task task = converter.buildObjectFromTO(dbObject);
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
        Task task = getByUri(taskUri);
        if (task != null) {
            task.setTaskStatus(taskStatus);
            update(task);
        }
    }

    @Override
    public boolean hasTasksWithStatus(TaskStatus taskStatus) {
        for (BasicDBObject dbObject : tasks.values()) {
            Task task = converter.buildObjectFromTO(dbObject);
            if (task.getTaskStatus() == taskStatus) {
                return true;
            }
        }
        return false;
    }
}
