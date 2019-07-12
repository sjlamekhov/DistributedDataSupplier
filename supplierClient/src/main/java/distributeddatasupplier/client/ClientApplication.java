package distributeddatasupplier.client;

import configuration.ConfigProvider;
import distributeddatasupplier.client.configuration.ClientConfigurationService;
import distributeddatasupplier.client.processing.AppenderTaskProcessor;
import distributeddatasupplier.client.processing.TaskProcessor;
import marshallers.*;
import messaging.FlowControl;
import messaging.Message;
import objects.Result;
import objects.Task;
import objects.TaskUri;

import java.io.IOException;
import java.util.Objects;

public class ClientApplication {

    public static void main(String[] args) throws IOException {
        System.out.println("supplierClient started");

        ClientConfigurationService configurationService = new ClientConfigurationService(
                ConfigProvider.getProperties()
        );

        TaskProcessor<Task, Result> taskProcessor = new AppenderTaskProcessor();
        Marshaller<Task> taskMarshaller = new IdOnlyTaskMarshaller();
        Marshaller<TaskUri> taskUriMarshaller = new TaskUriMarshaller();
        MessageMarshaller messageMarshaller = new MessageMarshaller(
                taskMarshaller, new ResultMarshaller(taskUriMarshaller)
        );
        Client client = new Client(configurationService.getHost(), configurationService.getPort());
        if (!client.isStarted()) {
            System.out.println("supplierClient did not start due to network issues");
            return;
        }
        int receiveCount = 0;
        int sentCount = 0;
        try {
            for (int i = 0; i < 8; i++) {
                String message = client.getMessage();
                receiveCount++;

                Message messageObject = messageMarshaller.unmarshall(message);

                System.out.println("received:\t" + message);
                Task task = messageObject.getTask();
                if (task == null) {
                    System.out.println("unexpected message, task == null");
                    client.stop();
                    return;
                } else if (Objects.equals(task.getUri(), Task.EMPTY_TASK.getUri())) {
                    client.sendMessage(messageMarshaller.marshall(new Message(FlowControl.BYE)));
                    client.stop();
                    return;
                }
                Message resultMessage = new Message(
                        taskProcessor.process(task),
                        i != 7 ? FlowControl.GETNEXTTASK : FlowControl.LAST
                );
                String messageMarshalled = messageMarshaller.marshall(resultMessage);
                client.sendMessage(messageMarshalled);
                sentCount++;
                System.out.println("sent:\t" + messageMarshalled);
            }
        } catch (IOException e) {
            System.out.println("supplierClient will be finished, server seems to be stopped");
        }
        System.out.println("sentCount=" + sentCount + "\treceiveCount=" + receiveCount);
        client.stop();
        System.out.println("supplierClient finished");
    }
}
