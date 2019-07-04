package persistence.converters;

import com.mongodb.BasicDBObject;
import objects.Result;
import objects.ResultUri;
import objects.Task;
import objects.TaskUri;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ResultConverter implements ObjectConverter<Result, BasicDBObject> {

    private static final Set<String> attributeNamesToSkip = new HashSet<>(Arrays.asList(
            "_id", "_taskId" ,"createTimestamp", "updateTimestamp"
    ));
    private final String tenantId;

    public ResultConverter(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Result buildObjectFromTO(BasicDBObject transferObject) {
        ResultUri resultUri = null;
        if (transferObject.containsField("_id")) {
            resultUri = new ResultUri(transferObject.getString("_id"), tenantId);
        }
        TaskUri taskUri = null;
        if (transferObject.containsField("_taskId")) {
            taskUri = new TaskUri(transferObject.getString("_id"), tenantId);
        }
        Map<String, String> attributes = transferObject.entrySet().stream()
                .filter(i -> !attributeNamesToSkip.contains(i.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue().toString()));
        Result result = new Result(resultUri, taskUri, attributes);
        if (transferObject.containsKey("createTimestamp")) {
            result.setCreateTimestamp(transferObject.getLong("createTimestamp"));
        }
        if (transferObject.containsKey("updateTimestamp")) {
            result.setUpdateTimestamp(transferObject.getLong("updateTimestamp"));
        }
        return result;
    }

    @Override
    public BasicDBObject buildToFromObject(Result result) {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (result.getUri() != null) {
            basicDBObject.put("_id", result.getUri().getId());
        }
        if (result.getTaskUri() != null) {
            basicDBObject.put("_taskId", result.getTaskUri().getId());
        }
        basicDBObject.put("createTimestamp", result.getCreateTimestamp());
        basicDBObject.put("updateTimestamp", result.getUpdateTimestamp());
        result.getFields().forEach(basicDBObject::put);
        return basicDBObject;
    }
}
