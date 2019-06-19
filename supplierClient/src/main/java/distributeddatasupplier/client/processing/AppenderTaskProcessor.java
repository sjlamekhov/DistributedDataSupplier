package distributeddatasupplier.client.processing;

import objects.Result;
import objects.Task;

import java.util.HashMap;

public class AppenderTaskProcessor implements TaskProcessor<Task, Result> {

    @Override
    public Result process(Task task) {
        return new Result(task.getUri(),
                new HashMap() {{
                    put(task.getUri().getId(), String.valueOf(task.hashCode()));
                }});
    }

}
