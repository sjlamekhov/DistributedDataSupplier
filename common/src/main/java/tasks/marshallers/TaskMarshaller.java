package tasks.marshallers;

import tasks.Task;

public interface TaskMarshaller {

    String mashallTask(Task task);
    Task unmarshallTask(String string);

}
