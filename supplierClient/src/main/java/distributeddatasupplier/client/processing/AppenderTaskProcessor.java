package distributeddatasupplier.client.processing;

import objects.Result;
import objects.ResultUri;
import objects.Task;

import java.util.HashMap;
import java.util.Objects;

public class AppenderTaskProcessor implements TaskProcessor<Task, Result> {

    @Override
    public Result process(Task task) {
        return new Result(
                new ResultUri(Objects.requireNonNull(task.getUri()).getTenantId()),
                task.getUri(),
                new HashMap() {{
                    put(task.getUri().getId(), String.valueOf(task.hashCode()));
                }});
    }

}
