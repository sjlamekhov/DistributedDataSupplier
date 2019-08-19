package objects;

public class ResultUri extends AbstractObjectUri {

    public ResultUri(String tenantId) {
        super(tenantId);
    }

    public ResultUri(String id, String tenantId) {
        super(id, tenantId);
    }

    public static ResultUri EMPTY = new ResultUri("EMPTY", "_null_");

}
