package marshallers;

import objects.Task;
import objects.TaskUri;

import java.util.Collections;

public class IdOnlyTaskMarshaller implements Marshaller<Task> {

    private static final String SEPARATOR = "@=@";

    @Override
    public String marshall(Task task) {
        return task.getUri().getId() + SEPARATOR + task.getUri().getTenantId();
    }

    @Override
    public Task unmarshall(String string) {
        String[] splitted = string.split(SEPARATOR);
        return new Task(new TaskUri(splitted[0], splitted[1]), Collections.EMPTY_MAP);
    }
}
