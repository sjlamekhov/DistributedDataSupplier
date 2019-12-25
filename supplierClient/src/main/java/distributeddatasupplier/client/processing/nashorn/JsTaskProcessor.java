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

import static objects.SerializationConstants.*;

public class JsTaskProcessor implements TaskProcessor<Task, Result> {

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
                resultParameters.put(EVAL_RESULT, evalResult.toString());
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

}
