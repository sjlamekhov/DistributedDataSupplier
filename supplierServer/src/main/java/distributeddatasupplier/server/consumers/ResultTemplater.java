package distributeddatasupplier.server.consumers;

import objects.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * template example:
 {"attributes": {"resultUri": "{{resultUri_field}}", "taskUri": "{{taskUri_field}}", #fields_map#}}
 */
public class ResultTemplater implements Function<Result, String> {

    public static final String MAP_POSTFIX = "_map";
    public static final String FIELD_POSTFIX = "_field";
    private final String template;
    private final Map<String, Function<Result,String>> extractors;

    public ResultTemplater(String template) {
        this.template = template;
        this.extractors = new HashMap<>();
        extractors.put("resultUri_field", r -> r.getUri().toString());
        extractors.put("taskUri_field", r -> r.getTaskUri().toString());
        extractors.put("fields_map", r -> {
            String marshalled = r.getFields().entrySet().stream()
                    .map(f -> String.format("\"%s\":\"%s\"", f.getKey(), f.getValue()))
                    .reduce((i1,i2) -> i1 + "," + "i2")
                    .orElse("");
            if (marshalled.isEmpty()) {
                return "\"fieldsMap\": null";
            }
            return marshalled;
        });
    }

    @Override
    public String apply(Result result) {
        if (!checkIfResultValid(result)) {
            return null;
        }
        String templatedResult = template;
        for (Map.Entry<String, Function<Result, String>> entry : extractors.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName.contains(MAP_POSTFIX)) {
                templatedResult = templatedResult.replace("#" + fieldName + "#", entry.getValue().apply(result));
            } else if (fieldName.contains(FIELD_POSTFIX)) {
                templatedResult = templatedResult.replace("{{" + fieldName + "}}", entry.getValue().apply(result));
            }
        }
        return templatedResult;
    }

    private static boolean checkIfResultValid(Result result) {
        return Objects.nonNull(result)
                && Objects.nonNull(result.getUri())
                && Objects.nonNull(result.getTaskUri())
                && Objects.nonNull(result.getFields());
    }
}
