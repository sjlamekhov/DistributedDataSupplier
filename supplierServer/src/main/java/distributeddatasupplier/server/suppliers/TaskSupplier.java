package distributeddatasupplier.server.suppliers;

import distributeddatasupplier.server.storage.TaskStorage;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

public class TaskSupplier {

    private final TaskStorage taskStorage;

    public TaskSupplier(TaskStorage taskStorage) {
        this.taskStorage = taskStorage;
    }

    public Task getTask() {
        return taskStorage.getAnyTask();
    }

    public void addTask(Task task) {
        taskStorage.addTask(task);
    }

    public boolean isEmpty() {
        return taskStorage.isEmpty(TaskStatus.NOT_STARTED);
    }

    //for backpressure
    public void markTaskAsFinished(TaskUri taskUri) {
        taskStorage.markTaskAsFinished(taskUri.getId());
    }
}
