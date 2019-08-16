package distributeddatasupplier.client.processing.htmlExtractor;

import java.util.*;
import java.util.function.Function;

import static objects.SerializationConstants.*;

public class ElementsWrapperInterpreter implements Function<String, List<Map<String, String>>> {

    private final String serializedExtractor;

    public ElementsWrapperInterpreter(String serializedExtractor) {
        this.serializedExtractor = serializedExtractor;
    }

    @Override
    public List<Map<String, String>> apply(String input) {
        String[] splitted = serializedExtractor.split(EXTRACTOR_SEPARATOR);
        if (splitted.length < 2) {
            return Collections.emptyList();
        }
        String firstExtractor = splitted[0];
        String mapperConfig = splitted[splitted.length - 1];
        Map<String, String> mapper = buildMapper(mapperConfig);
        ElementsWrapper elementsWrapper = ElementsWrapper.init(input, firstExtractor);
        for (int extractorIndex = 1; extractorIndex < splitted.length - 1; extractorIndex++) {
            elementsWrapper = elementsWrapper.extractToMany(splitted[extractorIndex]);
        }
        return elementsWrapper.extractToMaps(mapper);
    }

    private Map<String, String> buildMapper(String mapperConfig) {
        String stripped = mapperConfig.substring(EXTRACTOR_MAP_PREFIX.length(), mapperConfig.length() - EXTRACTOR_MAP_POSTFIX.length());
        String[] splitted = stripped.split(EXTRACTOR_MAP_SEPARATOR);
        Map<String, String> result = new HashMap<>();
        for (String entry : splitted) {
            String[] splittedEntry = entry.split(EXTRACTOR_MAP_CHAR);
            if (splittedEntry.length < 2) {
                continue;
            }
            result.put(splittedEntry[0], splittedEntry[1]);
        }
        return result;
    }

    public static String buildSerializedExtractor(List<String> queries, Map<String, String> extractors) {
        StringBuilder result = new StringBuilder();
        result.append(
                queries.stream().reduce((q1, q2) -> q1 + EXTRACTOR_SEPARATOR + q2).orElse("")
        );
        result.append(EXTRACTOR_SEPARATOR);
        result.append(EXTRACTOR_MAP_PREFIX);
        result.append(extractors.entrySet().stream()
                .map(k -> k.getKey() + EXTRACTOR_MAP_CHAR + k.getValue())
                .reduce((q1, q2) -> q1 + EXTRACTOR_MAP_SEPARATOR + q2).orElse(""));
        result.append(EXTRACTOR_MAP_POSTFIX);
        return result.toString();
    }
}
