package marshallers;

import objects.Result;
import objects.ResultUri;
import objects.Task;
import objects.TaskUri;

import java.util.HashMap;
import java.util.Map;

public class ResultMarshaller implements Marshaller<Result> {

    private static final String BODY_SEPARATOR = "_!_";
    private static final String FIELD_SEPARATOR = "_;_";
    private static final String KEYVALUE_SEPARATOR = "_->_";

    private static final String SEPARATOR = "@=@";

    private final String tenantId;
    private final Marshaller<TaskUri> taskUriMarshaller;

    public ResultMarshaller(String tenantId, Marshaller<TaskUri> taskUriMarshaller) {
        this.tenantId = tenantId;
        this.taskUriMarshaller = taskUriMarshaller;
    }

    @Override
    public String marshall(Result result) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(taskUriMarshaller.marshall(result.getTaskUri()));
        stringBuffer.append(BODY_SEPARATOR);
        if (result.getFields().isEmpty()) {
            stringBuffer.append(FIELD_SEPARATOR);
        }
        for (Map.Entry<String, String> entry : result.getFields().entrySet()) {
            stringBuffer.append(String.format("%s%s%s%s",
                    entry.getKey(), KEYVALUE_SEPARATOR,
                    entry.getValue(), FIELD_SEPARATOR));
        }

        return stringBuffer.toString();
    }

    @Override
    public Result unmarshall(String string) {
        String[] uriAndBody = string.split(BODY_SEPARATOR);
        TaskUri taskUri = taskUriMarshaller.unmarshall(uriAndBody[0]);
        Map<String, String> fields = new HashMap<>();
        String[] splitted = uriAndBody[1].split(FIELD_SEPARATOR);
        for (int i = 0; i < splitted.length; i++) {
            String[] splittedEntry = splitted[i].split(KEYVALUE_SEPARATOR);
            fields.put(splittedEntry[0], splittedEntry[1]);
        }
        return new Result(new ResultUri(tenantId), taskUri, fields);
    }
}
