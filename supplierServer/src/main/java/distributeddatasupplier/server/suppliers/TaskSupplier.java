package distributeddatasupplier.server.suppliers;

import tasks.Task;

public interface TaskSupplier {

    Task getTask();

    boolean isEmpty();

}
