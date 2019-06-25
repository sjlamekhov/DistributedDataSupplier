package distributeddatasupplier.server.services;

import dao.TaskDao;
import persistence.tasks.TaskPersistenceLayer;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

import java.util.*;

public class TaskService {

    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Task pollAnyTask() {
        Collection<Task> tasks = taskDao.getTasksByStatus(TaskStatus.NOT_STARTED, 1);
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

    public void add(Task task) {
        if (!Objects.equals(task, Task.EMPTY_TASK)) {
            taskDao.add(task);
        }
    }

    public void delete(TaskUri taskUri) {
        taskDao.deleteObject(taskUri);
    }

    public boolean isEmpty(TaskStatus taskStatus) {
        return !taskDao.hasTasksWithStatus(taskStatus);
    }

    public void markTaskAsFinished(TaskUri taskUri) {
        taskDao.setTaskStatus(taskUri, TaskStatus.FINISHED);
    }
}
