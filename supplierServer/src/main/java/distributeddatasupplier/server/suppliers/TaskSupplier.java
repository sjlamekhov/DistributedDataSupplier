package distributeddatasupplier.server.suppliers;

import distributeddatasupplier.server.services.TaskService;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

public class TaskSupplier {

    private final TaskService taskService;

    public TaskSupplier(TaskService taskService) {
        this.taskService = taskService;
    }

    public Task getTask() {
        return taskService.pollAnyTask();
    }

    public void addTask(Task task) {
        taskService.add(task);
    }

    public boolean isEmpty() {
        return taskService.isEmpty(TaskStatus.NOT_STARTED);
    }

    //for backpressure
    public void markTaskAsFinished(TaskUri taskUri) {
        taskService.markTaskAsFinished(taskUri);
    }
}
