package marshallers;

import objects.Task;

public interface TaskMarshaller {

    String mashallTask(Task task);
    Task unmarshallTask(String string);

}
