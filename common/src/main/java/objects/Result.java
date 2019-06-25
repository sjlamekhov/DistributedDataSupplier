package objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Result extends AbstractObject {

    private final Map<String, String> result;
    private final TaskUri taskUri;

    public Result(ResultUri resultUri, TaskUri taskUri, Map<String, String> keysAndValues) {
        super(resultUri);
        this.taskUri = taskUri;
        result = new HashMap<>(keysAndValues);
    }

    public ResultUri getUri() {
        return (ResultUri) objectUri;
    }

    public TaskUri getTaskUri() {
        return taskUri;
    }

    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(result);
    }

}
