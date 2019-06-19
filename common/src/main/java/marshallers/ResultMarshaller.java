package marshallers;

import objects.Result;

import java.util.HashMap;
import java.util.Map;

public class ResultMarshaller implements Marshaller<Result> {

    private static final String FIELD_SEPARATOR = "_;_";
    private static final String KEYVALUE_SEPARATOR = "_->_";

    @Override
    public String marshall(Result result) {
        StringBuilder stringBuffer = new StringBuilder();

        for (Map.Entry<String, String> entry : result.getFields().entrySet()) {
            System.out.println(String.format("%s %s -> %s %s",
                    entry.getKey().getClass(), entry.getKey(),
                    entry.getValue().getClass(), entry.getValue()));
            stringBuffer.append(String.format("%s%s%s%s",
                    entry.getKey(), KEYVALUE_SEPARATOR,
                    entry.getValue(), FIELD_SEPARATOR));
        }

        return stringBuffer.toString();
    }

    @Override
    public Result unmarshall(String string) {
        Map<String, String> fields = new HashMap<>();
        String[] splitted = string.split(FIELD_SEPARATOR);
        for (int i = 0; i < splitted.length; i++) {
            String[] splittedEntry = splitted[i].split(KEYVALUE_SEPARATOR);
            fields.put(splittedEntry[0], splittedEntry[1]);
        }
        return new Result(fields);
    }
}
