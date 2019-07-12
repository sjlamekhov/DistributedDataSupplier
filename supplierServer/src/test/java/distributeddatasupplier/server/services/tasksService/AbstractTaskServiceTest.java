package distributeddatasupplier.server.services.tasksService;

import distributeddatasupplier.server.services.TaskService;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTaskServiceTest {

    protected final String tenantId = "testTenantId";

    protected TaskService taskService;

    public AbstractTaskServiceTest() {
        init();
        this.taskService = getTaskService();
    }

    protected abstract TaskService getTaskService();

    protected void init() {
    }

    @Test
    public void createTaskTest() {
        TaskUri taskUri = new TaskUri(tenantId);
        Map<String, String> taskAttributes = new HashMap<>();
        taskAttributes.put("key1", "value1");
        taskAttributes.put("key2", "value2");
        Task task = new Task(taskUri, taskAttributes);
        Task addedTask = taskService.add(task);
        Assert.assertEquals(taskUri, addedTask.getUri());
        Assert.assertEquals(task.getTaskStatus(), addedTask.getTaskStatus());
        Assert.assertEquals(taskAttributes, addedTask.getTaskProperties());
    }

    @Test
    public void getResultTest() {
        TaskUri taskUri = new TaskUri(tenantId);
        Map<String, String> taskAttributes = new HashMap<>();
        taskAttributes.put("key1", "value1");
        taskAttributes.put("key2", "value2");
        Task task = new Task(taskUri, taskAttributes);
        taskService.add(task);
        Task gettedTask = taskService.getByUri(taskUri);
        Assert.assertEquals(taskUri, gettedTask.getUri());
        Assert.assertEquals(task.getTaskStatus(), gettedTask.getTaskStatus());
        Assert.assertEquals(taskAttributes, gettedTask.getTaskProperties());
    }

    @Test
    public void isEmptyAndMarkAsFinishedTest() {
        Assert.assertTrue(taskService.isEmpty(tenantId, TaskStatus.FINISHED));
        TaskUri taskUri = new TaskUri(tenantId);
        Map<String, String> taskAttributes = new HashMap<>();
        taskAttributes.put("key1", "value1");
        taskAttributes.put("key2", "value2");
        Task task = new Task(taskUri, taskAttributes);
        taskService.add(task);
        taskService.markTaskAsFinished(taskUri);
        Assert.assertFalse(taskService.isEmpty(tenantId, TaskStatus.FINISHED));
    }

    @Test
    public void pollTaskTest() {
        TaskUri taskUri = new TaskUri(tenantId);
        Map<String, String> taskAttributes = new HashMap<>();
        taskAttributes.put("key1", "value1");
        taskAttributes.put("key2", "value2");
        Task task = new Task(taskUri, taskAttributes);
        task.setTaskStatus(TaskStatus.NOT_STARTED);
        taskService.add(task);
        taskService.pollAnyTask(tenantId);
        Assert.assertFalse(taskService.isEmpty(tenantId, TaskStatus.IN_PROCESSING));
    }
}
