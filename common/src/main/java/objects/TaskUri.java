package objects;

public class TaskUri extends AbstractObjectUri {

    public TaskUri(String taskId) {
        super(taskId);
    }

    public TaskUri(String id, String tenantId) {
        super(id, tenantId);
    }

    public static TaskUri EMPTY = new TaskUri("EMPTY", null);

}
