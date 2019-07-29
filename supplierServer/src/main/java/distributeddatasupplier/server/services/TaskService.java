package distributeddatasupplier.server.services;

import dao.TaskDao;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

import java.util.*;
import java.util.stream.Collectors;

public class TaskService {

    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Task pollAnyTask(String tenantId) {
        Collection<Task> tasks = taskDao.getTasksByStatus(tenantId, TaskStatus.NOT_STARTED, 1);
        if (tasks.isEmpty()) {
            return Task.EMPTY_TASK;
        } else {
            Task task = tasks.iterator().next();
            taskDao.setTaskStatus(task.getUri(), TaskStatus.IN_PROCESSING);
            return task;
        }
    }

    public Task getByUri(TaskUri taskUri) {
        return taskDao.getByUri(taskUri);
    }

    public Task add(Task task) {
        if (!Objects.equals(task, Task.EMPTY_TASK)) {
            taskDao.add(task);
        }
        return task;
    }

    public void delete(TaskUri taskUri) {
        taskDao.deleteObject(taskUri);
    }

    public boolean isEmpty(String tenantId, TaskStatus taskStatus) {
        return !taskDao.hasTasksWithStatus(tenantId,  taskStatus);
    }

    public void markTaskAsFinished(TaskUri taskUri) {
        taskDao.setTaskStatus(taskUri, TaskStatus.FINISHED);
    }

    public Collection<TaskUri> getTaskUrisByStatus(String tenantId, TaskStatus taskStatus, int limit) {
        return taskDao.getTasksByStatus(tenantId, taskStatus, limit).stream()
                .map(Task::getUri)
                .collect(Collectors.toSet());
    }
}
