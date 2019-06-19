package objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Result {

    private final Map<String, String> result;
    private final TaskUri taskUri;

    public Result(TaskUri taskUri, Map<String, String> keysAndValues) {
        this.taskUri = taskUri;
        result = new HashMap<>(keysAndValues);
    }

    public TaskUri getTaskUri() {
        return taskUri;
    }

    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(result);
    }

}
