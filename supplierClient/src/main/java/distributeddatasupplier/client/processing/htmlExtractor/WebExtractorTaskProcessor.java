package distributeddatasupplier.client.processing.htmlExtractor;

import distributeddatasupplier.client.processing.TaskProcessor;
import objects.Result;
import objects.ResultUri;
import objects.Task;

import java.util.*;

import static objects.SerializationConstants.EXTRACTED;
import static objects.SerializationConstants.MULTIVALUE;

public class WebExtractorTaskProcessor implements TaskProcessor<Task, Result> {

    @Override
    public Result process(Task task) {
        ResultUri resultUri = new ResultUri(Objects.requireNonNull(task.getUri()).getTenantId());
        Map<String, String> taskProperties = task.getTaskProperties();
        String url = taskProperties.get("url");
        String extractor = taskProperties.get("extractor");
        if (null == url || null == extractor) {
            return new Result(resultUri, task.getUri(), Collections.emptyMap());
        }
        String content = UrlContentExtractor.getByUrl(url);
        if (null == content) {
            return new Result(resultUri, task.getUri(), Collections.emptyMap());
        }
        Map<String, String> resultContent = new HashMap<>();
        ElementsWrapperInterpreter elementsWrapperInterpreter = new ElementsWrapperInterpreter(extractor);
        List<Map<String, String>> extracted = elementsWrapperInterpreter.apply(content);
        resultContent.put(MULTIVALUE, "TRUE");
        resultContent.put(EXTRACTED, extracted.toString());
        return null;
    }

}
