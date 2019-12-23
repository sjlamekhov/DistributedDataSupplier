package distributeddatasupplier.client.processing.nashorn;

import distributeddatasupplier.client.processing.TaskProcessor;
import objects.Result;
import objects.Task;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Collections;
import java.util.Map;

public class JsTaskProcessor implements TaskProcessor<Task, Result> {

    private final ScriptEngine engine;

    public JsTaskProcessor() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    public Object eval(String toEval, Map<String, String> parameters) throws ScriptException, NoSuchMethodException {
        engine.eval(toEval);
        return ((Invocable) engine).invokeFunction("func");
    }

    @Override
    public Result process(Task input) {
        return null;
    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        JsTaskProcessor jsTaskProcessor = new JsTaskProcessor();
        jsTaskProcessor.eval("function func(name) {" +
                        "return 'Hello';" +
                        "}",
                Collections.emptyMap());
    }

}
