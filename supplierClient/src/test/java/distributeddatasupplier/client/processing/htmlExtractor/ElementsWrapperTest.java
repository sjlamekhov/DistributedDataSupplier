package distributeddatasupplier.client.processing.htmlExtractor;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementsWrapperTest {

    private static final String input = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<body>\n" +
            "<p class = \"someClass\">This is a paragraph of someClass.</p>\n" +
            "<p>This is a paragraph.</p>\n" +
            "</body>\n" +
            "</html>";

    @Test
    public void extractExistingTest() {
        Map<String, String> extractors = new HashMap<>();
        extractors.put("someClassKey", "p.someClass");
        List<Map<String, String>> result = ElementsWrapper
                .init(input, "html")
                .extractToMany("body")
                .extractToMaps(extractors);
        Assert.assertEquals(1, result.size());
        Map<String, String> resultInner = result.iterator().next();
        Assert.assertEquals(1, resultInner.size());
        Assert.assertEquals("This is a paragraph of someClass.", resultInner.get("someClassKey"));
    }

    @Test
    public void extractNotExistingTest() {
        Map<String, String> extractors = new HashMap<>();
        extractors.put("someClassKey", "p.someClass2");
        List<Map<String, String>> result = ElementsWrapper
                .init(input, "html")
                .extractToMany("body")
                .extractToMaps(extractors);
        Assert.assertTrue(result.isEmpty());
    }

}