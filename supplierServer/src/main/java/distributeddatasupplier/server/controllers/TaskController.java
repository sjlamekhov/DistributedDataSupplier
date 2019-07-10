package distributeddatasupplier.server.controllers;

import distributeddatasupplier.server.services.TaskService;
import objects.Task;
import objects.TaskUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

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
            return taskService.add(new Task(new TaskUri(tenantId), task.getTaskProperties()));
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

}
