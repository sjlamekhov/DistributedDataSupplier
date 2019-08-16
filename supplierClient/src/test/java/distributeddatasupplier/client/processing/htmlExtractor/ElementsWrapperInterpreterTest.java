package distributeddatasupplier.client.processing.htmlExtractor;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ElementsWrapperInterpreterTest {

    private static final String input = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<body>\n" +
            "<div>" +
            "<p class = \"someClass\">This is a paragraph of someClass.</p>\n" +
            "<p>This is a paragraph.</p>\n" +
            "</div>" +
            "</body>\n" +
            "</html>";

    @Test
    public void testSerialize() {
        String serialized = ElementsWrapperInterpreter
                .buildSerializedExtractor(Arrays.asList("body", "head"), new LinkedHashMap(){{
                    put("name", "a.name");
                    put("value", "a.value");
                }});
        Assert.assertEquals("body->head->[name=>a.name;value=>a.value]", serialized);
    }

    @Test
    public void testExtract() {
        ElementsWrapperInterpreter elementsWrapperInterpreter = new ElementsWrapperInterpreter(
                "body->div->[text=>p.someClass]"
        );
        List<Map<String, String>> extracted = elementsWrapperInterpreter.apply(input);
        Assert.assertEquals(1, extracted.size());
        Map<String, String> extractedMap = extracted.iterator().next();
        Assert.assertEquals(1, extractedMap.size());
        Assert.assertEquals("This is a paragraph of someClass.", extractedMap.get("text"));
    }

}