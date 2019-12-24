package distributeddatasupplier.client.processing.nashorn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import distributeddatasupplier.client.processing.TaskProcessor;
import objects.Result;
import objects.ResultUri;
import objects.Task;
import objects.TaskUri;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JsTaskProcessor implements TaskProcessor<Task, Result> {

    private static final String CODE_TO_EVAL = "codeToEval";
    private static final String JS_TASK_PROCESSOR = "jsTaskProcessor";
    private static final String TYPE = "type";
    private static final String PARAMETERS = "parameters";
    private final ScriptEngine engine;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsTaskProcessor() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    public Object eval(String toEval, JsonNode parameters) throws ScriptException, NoSuchMethodException, JsonProcessingException {
        engine.eval(toEval);
        return ((Invocable) engine).invokeFunction("func", parameters);
    }

    @Override
    public Result process(Task task) {
        if (isJsTaskProcessorAppliable(task)) {
            String codeToEval = task.getTaskProperties().get(CODE_TO_EVAL);
            String parameters = task.getTaskProperties().getOrDefault(PARAMETERS, "{}");
            Object evalResult = null;
            try {
                evalResult = eval(codeToEval, mapper.readTree(parameters));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != evalResult) {
                Map<String, String> resultParameters = new HashMap<>();
                resultParameters.put("evalResult", evalResult.toString());
                return new Result(
                        new ResultUri(Objects.requireNonNull(task.getUri()).getTenantId()),
                        task.getUri(),
                        resultParameters
                );
            }
        }
        return Result.EMPTY_RESULT;
    }

    private boolean isJsTaskProcessorAppliable(Task input) {
        if (null == input) {
            return false;
        }
        if (!Objects.equals(JS_TASK_PROCESSOR, input.getTaskProperties().get(TYPE))) {
            return false;
        }
        if (null == input.getTaskProperties().get(CODE_TO_EVAL)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsTaskProcessor jsTaskProcessor = new JsTaskProcessor();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("aKey", "aValue");
        parameters.put("bKey", "bValue");

        jsTaskProcessor.eval("function func(input) {" +
                        "print(input);" +
                        "return input;" +
                        "}",
                mapper.valueToTree(parameters));

        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put(TYPE, JS_TASK_PROCESSOR);
        taskProperties.put(PARAMETERS,"{\"a\": \"b\"}");
        taskProperties.put(CODE_TO_EVAL, "function func(input) {" +
                "print(input);" +
                "return input;" +
                "}");
        Task task = new Task(new TaskUri("tenant"), taskProperties);
        Result result = jsTaskProcessor.process(task);
        System.out.println(result);
    }

}
