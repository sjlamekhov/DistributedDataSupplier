package messaging;

import objects.Result;
import objects.Task;

public class Message {

    private Task task;
    private Result result;
    private final FlowControl flowControl;

    public Message(FlowControl flowControl) {
        this.task = null;
        this.result = null;
        this.flowControl = flowControl;
    }

    public Message(Task task, FlowControl flowControl) {
        this.task = task;
        this.result = null;
        this.flowControl = flowControl;
    }

    public Message(Result result, FlowControl flowControl) {
        this.task = null;
        this.result = result;
        this.flowControl = flowControl;
    }

    public Task getTask() {
        return task;
    }

    public Result getResult() {
        return result;
    }

    public FlowControl getFlowControl() {
        return flowControl;
    }
}
