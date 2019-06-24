package distributeddatasupplier.server.storage;

import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

public interface TaskStorage {

    Task getAnyTask();

    Task getTaskById(String taskId);

    void addTask(Task task);

    void deleteTaskById(String taskId);

    boolean isEmpty(TaskStatus taskStatus);

    void markTaskAsFinished(String taskId);
}
