package distributeddatasupplier.client.processing.htmlExtractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementsWrapper {

    private final Elements elements;

    public ElementsWrapper(Elements elements) {
        this.elements = elements;
    }

    public static ElementsWrapper init(String content, String query) {
        Document doc = Jsoup.parse(content);
        return new ElementsWrapper(doc.select(query));
    }

    public ElementsWrapper extractToMany(String query) {
        List<Element> result = new ArrayList<>();
        for (Element element : elements) {
            result.addAll(element.select(query));
        }
        return new ElementsWrapper(new Elements(result));
    }

    public List<Map<String, String>> extractToMaps(Map<String, String> keyToQuery) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Element element : elements) {
            Map<String, String> local = new HashMap<>();
            for (Map.Entry<String, String> extractorEntry : keyToQuery.entrySet()) {
                Elements extracted = element.select(extractorEntry.getValue());
                if (!extracted.isEmpty()) {
                    local.put(extractorEntry.getKey(), extracted.first().text());
                }
            }
            if (!local.isEmpty()) {
                result.add(local);
            }
        }
        return result;
    }

}