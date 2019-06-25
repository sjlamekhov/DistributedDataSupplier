package distributeddatasupplier.client.processing;

import objects.Result;
import objects.ResultUri;
import objects.Task;

import java.util.HashMap;

public class AppenderTaskProcessor implements TaskProcessor<Task, Result> {

    @Override
    public Result process(Task task) {
        return new Result(
                new ResultUri(),
                task.getUri(),
                new HashMap() {{
                    put(task.getUri().getId(), String.valueOf(task.hashCode()));
                }});
    }

}
