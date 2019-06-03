package distributeddatasupplier.client.processing;

import tasks.Task;

public class AppenderTaskProcessor implements TaskProcessor<Task, String> {

    @Override
    public String process(Task task) {
        return task.getTaskId() + "_" + task.hashCode();
    }

}
