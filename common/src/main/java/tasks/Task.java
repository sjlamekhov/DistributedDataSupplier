package tasks;

import java.util.Collections;
import java.util.Map;

public class Task {

    private final String taskId;
    private final Map<String, String> taskProperties;

    public Task(String taskId, Map<String, String> taskProperties) {
        this.taskId = taskId;
        this.taskProperties = taskProperties;
    }

    public String getTaskId() {
        return taskId;
    }

    public Map<String, String> getTaskProperties() {
        return Collections.unmodifiableMap(taskProperties);
    }
}
