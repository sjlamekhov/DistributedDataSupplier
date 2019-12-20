package distributeddatasupplier.server.controllers;

import distributeddatasupplier.server.services.TaskService;
import objects.Task;
import objects.TaskStatus;
import objects.TaskUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class TaskController {

    private static final int RESPONSE_SIZE_LIMIT = 16;

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/tenants/{tenantId}/tasks/byStatus/{status}",
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Collection<TaskUri> getTasksByStatus(@PathVariable("tenantId") String tenantId,
                                                @PathVariable("status") String status) {
        TaskStatus taskStatus = TaskStatus.valueOf(status);
        return taskService.getTaskUrisByStatus(tenantId, taskStatus, RESPONSE_SIZE_LIMIT);
    }

    @RequestMapping(value = "/tenants/{tenantId}/tasks/{taskId}",
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Task getTaskById(@PathVariable("tenantId") String tenantId,
                            @PathVariable("taskId") String taskId) {
        try {
            return taskService.getByUri(new TaskUri(taskId, tenantId));
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    @RequestMapping(value = "/tenants/{tenantId}/tasks",
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Task addTask(@PathVariable("tenantId") String tenantId,
                            @RequestBody Task task) {
        try {
            TaskUri uriToAdd = null == task.getUri() ? new TaskUri(tenantId) : task.getUri();
            Task taskToAdd = new Task(uriToAdd, task.getTaskProperties());
            if (task.getTaskStatus() != null) {
                taskToAdd.setTaskStatus(task.getTaskStatus());
            }
            return taskService.add(taskToAdd);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

}
