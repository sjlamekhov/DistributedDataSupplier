package marshallers;

import messaging.FlowControl;
import messaging.Message;
import objects.Result;
import objects.Task;

public class MessageMarshaller implements Marshaller<Message> {

    private static final String SEPARATOR = "_=_";
    private static final String TASK_PREFIX = "T";
    private static final String RESULT_PREFIX = "R";
    private static final String FLOW_PREFIX = "F";

    private final Marshaller<Task> taskMarshaller;
    private final Marshaller<Result> resultMarshaller;

    public MessageMarshaller(Marshaller<Task> taskMarshaller, Marshaller<Result> resultMarshaller) {
        this.taskMarshaller = taskMarshaller;
        this.resultMarshaller = resultMarshaller;
    }

    //T_SEPARATOR_task_SEPARATOR_flowControl
    //R_SEPARATOR_result_SEPARATOR_flowControl
    @Override
    public String marshall(Message object) {
        if (object.getTask() != null) {
            return String.format("%s%s%s%s%s",
                    TASK_PREFIX, SEPARATOR,
                    taskMarshaller.marshall(object.getTask()), SEPARATOR,
                    object.getFlowControl());
        } else if (object.getResult() != null) {
            return String.format("%s%s%s%s%s",
                    RESULT_PREFIX, SEPARATOR,
                    resultMarshaller.marshall(object.getResult()), SEPARATOR,
                    object.getFlowControl());
        } else if (object.getFlowControl() != null){
            return String.format("%s%s%s%s%s",
                    FLOW_PREFIX,
                    SEPARATOR, "", SEPARATOR,
                    object.getFlowControl());
        }
        return null;
    }

    @Override
    public Message unmarshall(String string) {
        String[] splitted = string.split(SEPARATOR);
        if (splitted.length != 3) {
            return null;
        }
        switch (splitted[0]) {
            case TASK_PREFIX:
                return new Message(
                        taskMarshaller.unmarshall(splitted[1]),
                        FlowControl.valueOf(splitted[2]));
            case RESULT_PREFIX:
                return new Message(
                        resultMarshaller.unmarshall(splitted[1]),
                        FlowControl.valueOf(splitted[2]));
            case FLOW_PREFIX:
                return new Message(FlowControl.valueOf(splitted[2]));
        }
        return null;
    }
}
