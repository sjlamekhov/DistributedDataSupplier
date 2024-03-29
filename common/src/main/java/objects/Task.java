package objects;

import java.util.Collections;
import java.util.Map;

public class Task extends AbstractObject {

    private TaskStatus taskStatus;
    private final Map<String, String> taskProperties;

    public Task(TaskUri taskUri, Map<String, String> taskProperties) {
        super(taskUri);
        taskStatus = TaskStatus.NOT_STARTED;
        this.taskProperties = taskProperties;
    }

    public TaskUri getUri() {
        return (TaskUri) objectUri;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Map<String, String> getTaskProperties() {
        return Collections.unmodifiableMap(taskProperties);
    }

    public static Task EMPTY_TASK = new Task(TaskUri.EMPTY, Collections.emptyMap());
}
