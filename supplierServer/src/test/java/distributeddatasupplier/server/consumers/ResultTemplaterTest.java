package distributeddatasupplier.server.consumers;

import marshallers.ListMapMarshaller;
import objects.Result;
import objects.ResultUri;
import objects.TaskUri;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static objects.SerializationConstants.MULTIVALUE;
import static objects.SerializationConstants.VALUES;

public class ResultTemplaterTest {

    private final static String tenantId = "testTenantId";
    private final static String emptyTemplate = "{}";
    private final static String fullTemplate = "{\"attributes\": {" +
            "\"resultUri\": \"{{resultUri_field}}\", " +
            "\"taskUri\": \"{{taskUri_field}}\", " +
            "#fields_map#}}";

    @Test
    public void emptyTemplateTest() {
        ResultTemplater resultTemplater = new ResultTemplater(emptyTemplate);
        Map<String, String> keysAndValues = new HashMap<>();
        keysAndValues.put("key1", "value1");
        Result result = new Result(new ResultUri(tenantId), new TaskUri(tenantId), keysAndValues);
        String appliedTemplate = resultTemplater.apply(result);
        Assert.assertEquals("[" + emptyTemplate + "]", appliedTemplate);
    }

    @Test
    public void fullTemplateTest() {
        ResultTemplater resultTemplater = new ResultTemplater(fullTemplate);
        Map<String, String> keysAndValues = new HashMap<>();
        keysAndValues.put("key", "value");
        Result result = new Result(new ResultUri(tenantId), new TaskUri(tenantId), keysAndValues);
        String appliedTemplate = resultTemplater.apply(result);
        Assert.assertTrue(appliedTemplate.contains(result.getUri().getId()));
        Assert.assertTrue(appliedTemplate.contains(result.getTaskUri().getId()));
        Assert.assertTrue(appliedTemplate.contains("key") && appliedTemplate.contains("value"));
    }

    @Test
    public void fullTemplateMultiResultTest() {
        ResultTemplater resultTemplater = new ResultTemplater(fullTemplate);
        ListMapMarshaller listMapMarshaller = new ListMapMarshaller();
        Map<String, String> keysAndValues = new HashMap<>();
        keysAndValues.put(MULTIVALUE, "TRUE");
        keysAndValues.put(VALUES, listMapMarshaller.marshall(Arrays.asList(
                new HashMap() {{
                    put("key1", "value1");
                }},
                new HashMap() {{
                    put("key2", "value2");
                }}
        )));
        Result result = new Result(new ResultUri(tenantId), new TaskUri(tenantId), keysAndValues);
        String appliedTemplate = resultTemplater.apply(result);
        Assert.assertTrue(appliedTemplate.contains(result.getUri().getId()));
        Assert.assertTrue(appliedTemplate.contains(result.getTaskUri().getId()));
        Assert.assertTrue(appliedTemplate.contains("key") && appliedTemplate.contains("value"));
    }

}