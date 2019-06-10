package distributeddatasupplier.server.storage;

import tasks.Task;

public interface TaskStorage {

    Task getAnyTask();

    Task getTaskById(String taskId);

    void addTask(Task task);

    void deleteTaskById(String taskId);

    boolean isEmpty();

}
