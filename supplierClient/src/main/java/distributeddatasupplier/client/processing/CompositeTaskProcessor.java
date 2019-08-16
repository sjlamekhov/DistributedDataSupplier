package distributeddatasupplier.client.processing;

import objects.Result;
import objects.ResultUri;
import objects.Task;
import objects.TaskType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static objects.SerializationConstants.RESULT_STATUS;
import static objects.SerializationConstants.TASK_TYPE;

public class CompositeTaskProcessor implements TaskProcessor<Task, Result> {

    private final Map<TaskType, TaskProcessor<Task, Result>> processors;
    private final TaskProcessor<Task, Result> defaultTaskProcessor;

    public CompositeTaskProcessor() {
        this.processors = new HashMap<>();
        this.defaultTaskProcessor = task -> new Result(
                new ResultUri(
                        Objects.requireNonNull(task.getUri()).getTenantId()),
                task.getUri(),
                new HashMap() {{
                    put(RESULT_STATUS, "expected processor was not found");
                }});
    }

    public CompositeTaskProcessor(TaskProcessor<Task, Result> defaultTaskProcessor) {
        this.processors = new HashMap<>();
        this.defaultTaskProcessor = defaultTaskProcessor;
    }

    @Override
    public Result process(Task task) {
        TaskProcessor<Task, Result> taskProcessor = getTaskProcessorInternal(
                TaskType.valueOf(
                        task.getTaskProperties().getOrDefault(TASK_TYPE, "DEFAULT")
                )
        );
        return taskProcessor.process(task);
    }

    private TaskProcessor<Task, Result> getTaskProcessorInternal(TaskType taskType) {
        return processors.getOrDefault(taskType, defaultTaskProcessor);
    }

    public void registerProcessor(TaskType taskType, TaskProcessor<Task, Result> taskProcessor) {
        processors.put(taskType, taskProcessor);
    }

}
