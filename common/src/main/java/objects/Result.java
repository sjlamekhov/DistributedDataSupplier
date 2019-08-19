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

    public static Result EMPTY_RESULT = new Result(ResultUri.EMPTY, TaskUri.EMPTY, Collections.emptyMap());

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result1 = (Result) o;

        if (result != null ? !result.equals(result1.result) : result1.result != null) return false;
        return taskUri != null ? taskUri.equals(result1.taskUri) : result1.taskUri == null;
    }

    @Override
    public int hashCode() {
        int result1 = result != null ? result.hashCode() : 0;
        result1 = 31 * result1 + (taskUri != null ? taskUri.hashCode() : 0);
        return result1;
    }
}
