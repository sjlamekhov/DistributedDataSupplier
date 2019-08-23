package marshallers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMapMarshaller implements Marshaller<List<Map<String, String>>> {

    private static final String LIST_SEPARATOR = "!=!";

    private Marshaller<Map<String, String>> mapMarshaller;

    public ListMapMarshaller() {
        this.mapMarshaller = new MapMarshaller();
    }

    @Override
    public String marshall(List<Map<String, String>> object) {
        return object.stream()
                .map(m -> mapMarshaller.marshall(m))
                .reduce((i1,i2) -> i1 + LIST_SEPARATOR + i2)
                .orElse("");
    }

    @Override
    public List<Map<String, String>> unmarshall(String string) {
        List<Map<String, String>> result = new ArrayList<>();
        String[] maps = string.split(LIST_SEPARATOR);
        for (String map : maps) {
            result.add(mapMarshaller.unmarshall(map));
        }
        return result;
    }
}
