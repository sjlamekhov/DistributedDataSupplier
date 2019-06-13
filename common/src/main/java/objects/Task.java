package objects;

import java.util.Collections;
import java.util.Map;

public class Task extends AbstractObject {

    private final Map<String, String> taskProperties;

    public Task(String taskId, Map<String, String> taskProperties) {
        super(new TaskUri(taskId));
        this.taskProperties = taskProperties;
    }

    public TaskUri getUri() {
        return (TaskUri) objectUri;
    }

    public Map<String, String> getTaskProperties() {
        return Collections.unmodifiableMap(taskProperties);
    }

    public static Task EMPTY_TASK = new Task("EMPTY", Collections.emptyMap());
}
