package distributeddatasupplier.client.processing.htmlExtractor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;


//TODO: implement
public class ElementsWrapperInterpreter implements Function<String, Map<String, String>> {

    private final String serializedExtractor;

    public ElementsWrapperInterpreter(String serializedExtractor) {
        this.serializedExtractor = serializedExtractor;
    }

    @Override
    public Map<String, String> apply(String input) {
        return Collections.emptyMap();
    }
}
