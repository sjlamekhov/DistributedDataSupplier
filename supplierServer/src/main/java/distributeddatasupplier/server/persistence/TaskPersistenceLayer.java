package distributeddatasupplier.server.persistence;

import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

import java.util.Collection;

public interface TaskPersistenceLayer extends PersistenceLayer<TaskUri, Task> {

    Collection<Task> getTasksByStatus(TaskStatus taskStatus, int limit);
    void setTaskStatus(TaskUri taskUri, TaskStatus taskStatus);
    boolean hasTasksWithStatus(TaskStatus taskStatus);
}
