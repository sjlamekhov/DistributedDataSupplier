package distributeddatasupplier.client.processing.nashorn;

import com.fasterxml.jackson.databind.ObjectMapper;
import objects.Result;
import objects.Task;
import objects.TaskUri;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static objects.SerializationConstants.*;

public class JsTaskProcessorTest {

    private final ObjectMapper mapper;
    private final JsTaskProcessor jsTaskProcessor;

    public JsTaskProcessorTest() {
        this.mapper = new ObjectMapper();
        this.jsTaskProcessor = new JsTaskProcessor();
    }

    @Test
    public void testEval() throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("aKey", "aValue");
        parameters.put("bKey", "bValue");

        Object resultOfEval = jsTaskProcessor.eval("function func(input) {" +
                        "print(input);" +
                        "return input;" +
                        "}",
                mapper.valueToTree(parameters));
        Assert.assertNotNull(resultOfEval);
    }

    @Test
    public void test() {
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put(TYPE, JS_TASK_PROCESSOR);
        taskProperties.put(PARAMETERS,"{\"a\": \"b\"}");
        taskProperties.put(CODE_TO_EVAL, "function func(input) {" +
                "print(input);" +
                "return input;" +
                "}");
        Task task = new Task(new TaskUri("tenant"), taskProperties);
        Result result = jsTaskProcessor.process(task);
        Assert.assertNotNull(result);
        Assert.assertEquals(task.getUri(), result.getTaskUri());
        Assert.assertNotNull(result.getUri());
        Assert.assertEquals("{\"a\":\"b\"}", result.getFields().get(EVAL_RESULT));
    }

}