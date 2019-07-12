package persistence.converters;

import com.mongodb.BasicDBObject;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskConverter implements ObjectConverter<Task, BasicDBObject> {

    private static final Set<String> attributeNamesToSkip = new HashSet<>(Arrays.asList(
            "_id", "createTimestamp", "updateTimestamp", "taskStatus"
    ));
    private final String tenantId;

    public TaskConverter(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Task buildObjectFromTO(BasicDBObject transferObject) {
        TaskUri taskUri = null;
        if (transferObject.containsField("_id")) {
            taskUri = new TaskUri(transferObject.getString("_id"), tenantId);
        }
        Map<String, String> attributes = transferObject.entrySet().stream()
                .filter(i -> !attributeNamesToSkip.contains(i.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue().toString()));
        Task task = new Task(taskUri, attributes);
        if (transferObject.containsKey("createTimestamp")) {
            task.setCreateTimestamp(transferObject.getLong("createTimestamp"));
        }
        if (transferObject.containsKey("updateTimestamp")) {
            task.setUpdateTimestamp(transferObject.getLong("updateTimestamp"));
        }
        if (transferObject.containsKey("taskStatus")) {
            task.setTaskStatus(TaskStatus.valueOf(transferObject.getString("taskStatus")));
        }
        return task;
    }

    @Override
    public BasicDBObject buildToFromObject(Task task) {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (task.getUri() != null) {
            basicDBObject.put("_id", task.getUri().getId());
        }
        basicDBObject.put("createTimestamp", task.getCreateTimestamp());
        basicDBObject.put("updateTimestamp", task.getUpdateTimestamp());
        basicDBObject.put("taskStatus", task.getTaskStatus().toString());
        task.getTaskProperties().forEach(basicDBObject::put);
        return basicDBObject;
    }
}
