package distributeddatasupplier.client.processing.htmlExtractor;

import distributeddatasupplier.client.processing.TaskProcessor;
import objects.Result;
import objects.ResultUri;
import objects.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WebExtractorTaskProcessor implements TaskProcessor<Task, Result> {

    @Override
    public Result process(Task task) {
        ResultUri resultUri = new ResultUri(Objects.requireNonNull(task.getUri()).getTenantId());
        Map<String, String> taskProperties = task.getTaskProperties();
        String url = taskProperties.get("url");
        String extractor = taskProperties.get("extractor");
        if (null == url || null == extractor) {
            return new Result(resultUri, task.getUri(), new HashMap<>());
        }
        String content = UrlContentExtractor.getByUrl(url);
        if (null == content) {
            return new Result(resultUri, task.getUri(), new HashMap<>());
        }
        //TODO: finish implementation
        return null;
    }

}
