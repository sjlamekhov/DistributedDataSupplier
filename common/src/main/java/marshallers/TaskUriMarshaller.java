package marshallers;

import objects.TaskUri;

public class TaskUriMarshaller implements Marshaller<TaskUri> {

    private static final String SEPARATOR = "@=@";

    @Override
    public String marshall(TaskUri object) {
        return object.getId() + SEPARATOR + object.getTenantId();
    }

    @Override
    public TaskUri unmarshall(String string) {
        String[] splitted = string.split(SEPARATOR);
        return new TaskUri(splitted[0], splitted[1]);
    }
}
