package marshallers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapMarshaller implements Marshaller<Map<String, String>> {

    private static final String KEYVALUE_SEPARATOR = "->";
    private static final String ENTRY_SEPARATOR = "@=@";

    @Override
    public String marshall(Map<String, String> object) {
        return object.entrySet().stream()
                .map(kv -> kv.getKey() + KEYVALUE_SEPARATOR + kv.getValue())
                .reduce((i1, i2) -> i1 + ENTRY_SEPARATOR + i2)
                .orElse("");
    }

    @Override
    public Map<String, String> unmarshall(String string) {
        Map<String, String> result = new HashMap<>();
        String[] entries = string.split(ENTRY_SEPARATOR);
        for (String entry : entries) {
            String[] keyAndValue = entry.split(KEYVALUE_SEPARATOR);
            if (2 != keyAndValue.length) {
                return Collections.emptyMap();
            }
            result.put(keyAndValue[0], keyAndValue[1]);
        }
        return result;
    }
}
