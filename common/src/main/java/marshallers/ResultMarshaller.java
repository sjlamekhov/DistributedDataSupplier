package marshallers;

import objects.Result;
import objects.ResultUri;
import objects.TaskUri;

import java.util.HashMap;
import java.util.Map;

import static marshallers.MarshallerConstants.*;

public class ResultMarshaller implements Marshaller<Result> {

    private final Marshaller<ResultUri> resultUriMarshaller;
    private final Marshaller<TaskUri> taskUriMarshaller;

    public ResultMarshaller(Marshaller<ResultUri> resultUriMarshaller,
                            Marshaller<TaskUri> taskUriMarshaller) {
        this.resultUriMarshaller = resultUriMarshaller;
        this.taskUriMarshaller = taskUriMarshaller;
    }

    @Override
    public String marshall(Result result) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(resultUriMarshaller.marshall(result.getUri()));
        stringBuffer.append(RESULT_URI_SEPARATOR);
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
        String[] resultUriAndRest = string.split(RESULT_URI_SEPARATOR);
        ResultUri resultUri = resultUriMarshaller.unmarshall(resultUriAndRest[0]);
        String[] uriAndBody = resultUriAndRest[1].split(BODY_SEPARATOR);
        TaskUri taskUri = taskUriMarshaller.unmarshall(uriAndBody[0]);
        Map<String, String> fields = new HashMap<>();
        String[] splitted = uriAndBody[1].split(FIELD_SEPARATOR);
        for (int i = 0; i < splitted.length; i++) {
            String[] splittedEntry = splitted[i].split(KEYVALUE_SEPARATOR);
            fields.put(splittedEntry[0], splittedEntry[1]);
        }
        return new Result(resultUri, taskUri, fields);
    }
}
