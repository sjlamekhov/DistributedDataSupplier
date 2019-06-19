package objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Result {

    private final Map<String, String> result;

    public Result(Map<String, String> keysAndValues) {
        result = new HashMap<>(keysAndValues);
    }

    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(result);
    }

}
