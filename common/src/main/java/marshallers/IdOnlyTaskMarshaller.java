package marshallers;

import objects.Task;

import java.util.Collections;

public class IdOnlyTaskMarshaller implements Marshaller<Task> {

    @Override
    public String marshall(Task task) {
        return task.getUri().getId();
    }

    @Override
    public Task unmarshall(String string) {
        return new Task(string, Collections.EMPTY_MAP);
    }
}
