package distributeddatasupplier.server.controllers;

import distributeddatasupplier.server.services.ResultService;
import objects.Result;
import objects.ResultUri;
import objects.TaskUri;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ResultControllerTest {

    private final static String testTenantId = "testTenantId";

    @Autowired
    private ResultService resultService;

    @Autowired
    private ResultController resultController;

    @Test
    public void getById() {
        Result result = new Result(new ResultUri(testTenantId), new TaskUri(testTenantId), Collections.emptyMap());
        Assert.assertNull(resultController.getResultById(testTenantId, result.getUri().getId()));
        resultService.add(result);
        Assert.assertNotNull(resultController.getResultById(testTenantId, result.getUri().getId()));
    }

    @Test
    public void getResults() {
        int maxNumberOfTasks = resultController.RESPONSE_SIZE_LIMIT;
        List<ResultUri> resultUris = new ArrayList<>();
        for (int i = 0; i < maxNumberOfTasks; i++) {
            Result result = new Result(new ResultUri(testTenantId), new TaskUri(testTenantId), Collections.emptyMap());
            resultService.add(result);
            resultUris.add(result.getUri());
        }
        Collection<ResultUri> responseUris = resultController.getResults(testTenantId, null);
        Assert.assertEquals(maxNumberOfTasks,  responseUris.size());
        System.out.println(responseUris);
        System.out.println(resultUris);
        Assert.assertTrue(responseUris.containsAll(resultUris));
        responseUris = resultController.getResults(testTenantId, maxNumberOfTasks / 2);
        Assert.assertEquals(maxNumberOfTasks/2,  responseUris.size());
        Assert.assertTrue(resultUris.containsAll(responseUris));
    }

}