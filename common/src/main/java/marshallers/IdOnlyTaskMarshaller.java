package marshallers;

import objects.Task;

import java.util.Collections;

public class IdOnlyTaskMarshaller implements TaskMarshaller {

    @Override
    public String mashallTask(Task task) {
        return task.getUri().getId();
    }

    @Override
    public Task unmarshallTask(String string) {
        return new Task(string, Collections.EMPTY_MAP);
    }
}
