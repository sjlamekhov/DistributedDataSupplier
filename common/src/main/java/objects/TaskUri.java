package objects;

public class TaskUri extends AbstractObjectUri {

    public TaskUri(String tenantId) {
        super(tenantId);
    }

    public TaskUri(String id, String tenantId) {
        super(id, tenantId);
    }

    public static TaskUri EMPTY = new TaskUri("EMPTY", "_null_");

}
