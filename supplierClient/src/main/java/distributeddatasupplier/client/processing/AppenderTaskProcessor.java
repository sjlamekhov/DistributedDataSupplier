package distributeddatasupplier.client.processing;

import objects.Task;

public class AppenderTaskProcessor implements TaskProcessor<Task, String> {

    @Override
    public String process(Task task) {
        return task.getUri().getId() + "_" + task.hashCode();
    }

}
