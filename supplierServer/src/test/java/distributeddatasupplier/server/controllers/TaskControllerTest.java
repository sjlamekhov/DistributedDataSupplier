package distributeddatasupplier.server.controllers;

import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TaskControllerTest {

    private final static String testTenantId = "testTenantId";

    @Autowired
    private TaskController taskController;

    @Test
    public void addAndGetTask() {
        TaskUri taskUri = new TaskUri(testTenantId);
        Map<String, String> taskAttributes = new HashMap<>();
        taskAttributes.put("attribute", "value");
        Task task = new Task(taskUri, taskAttributes);
        taskController.addTask(testTenantId, task);

        Task gettedTask = taskController.getTaskById(testTenantId, taskUri.getId());
        Assert.assertNotNull(gettedTask);
        Assert.assertEquals(taskUri, gettedTask.getUri());
        Assert.assertEquals(task.getTaskStatus(), gettedTask.getTaskStatus());
        Assert.assertEquals(task.getTaskProperties(), gettedTask.getTaskProperties());
    }

    @Test
    public void addAndGetByStatusTask() {
        List<TaskStatus> statusesToTest = Arrays.asList(
                TaskStatus.NOT_STARTED,
                TaskStatus.IN_PROCESSING,
                TaskStatus.FINISHED,
                TaskStatus.FAILED);
        for (TaskStatus taskStatus : statusesToTest) {
            TaskUri taskUri = new TaskUri(testTenantId);
            Task task = new Task(taskUri, Collections.emptyMap());
            task.setTaskStatus(taskStatus);
            taskController.addTask(testTenantId, task);
            Collection<TaskUri> getted = taskController.getTasksByStatus(testTenantId, taskStatus.toString());
            Assert.assertTrue(getted.contains(taskUri));
        }



    }

}
