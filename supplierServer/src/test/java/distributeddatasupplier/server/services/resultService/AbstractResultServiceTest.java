package distributeddatasupplier.server.services.resultService;

import distributeddatasupplier.server.services.ResultService;
import objects.Result;
import objects.ResultUri;
import objects.TaskUri;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractResultServiceTest {

    protected final String tenantId = "testTenantId";

    protected ResultService resultService;

    public AbstractResultServiceTest() {
        init();
        this.resultService = getResultService();
    }

    protected abstract ResultService getResultService();

    protected void init() {}

    @Test
    public void createResultTest() {
        ResultUri resultUri = new ResultUri(tenantId);
        TaskUri taskUri = new TaskUri(tenantId);
        Map<String, String> taskAttributes = new HashMap<>();
        taskAttributes.put("key1", "value1");
        taskAttributes.put("key2", "value2");
        Result result = new Result(resultUri, taskUri, taskAttributes);
        Result addedResult = resultService.add(result);
        Assert.assertEquals(resultUri, addedResult.getUri());
        Assert.assertEquals(taskUri, addedResult.getTaskUri());
        Assert.assertEquals(taskAttributes, addedResult.getFields());
    }

    @Test
    public void getResultTest() {
        ResultUri resultUri = new ResultUri(tenantId);
        TaskUri taskUri = new TaskUri(tenantId);
        Map<String, String> taskAttributes = new HashMap<>();
        taskAttributes.put("key1", "value1");
        taskAttributes.put("key2", "value2");
        Result result = new Result(resultUri, taskUri, taskAttributes);
        resultService.add(result);
        Result gettedResult = resultService.getByUri(resultUri);
        Assert.assertEquals(resultUri, gettedResult.getUri());
        Assert.assertEquals(taskUri, gettedResult.getTaskUri());
        Assert.assertEquals(taskAttributes, gettedResult.getFields());
    }

}