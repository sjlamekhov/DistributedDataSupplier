package distributeddatasupplier.client;

import configuration.ConfigurationService;
import distributeddatasupplier.client.processing.AppenderTaskProcessor;
import distributeddatasupplier.client.processing.TaskProcessor;
import objects.Task;
import marshallers.IdOnlyTaskMarshaller;
import marshallers.TaskMarshaller;

import java.io.IOException;
import java.util.Objects;

public class ClientApplication {
    public static void main(String[] args) throws IOException {
        System.out.println("supplierClient started");

        ConfigurationService configurationService = new ConfigurationService();
        TaskProcessor<Task, String> taskProcessor = new AppenderTaskProcessor();
        TaskMarshaller taskMarshaller = new IdOnlyTaskMarshaller();
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
                System.out.println("received:\t" + message.split(";")[0]);
                Task task = taskMarshaller.unmarshallTask(message);
                if (Objects.equals(task.getUri(), Task.EMPTY_TASK.getUri())) {
                    client.sendMessage(";BYE");
                    client.stop();
                    return;
                }
                String processed = taskProcessor.process(task) + (i != 7 ? ";GETNEXTTASK" : ";LAST");
                client.sendMessage(processed);
                sentCount++;
                System.out.println("sent:\t" + processed);
            }
        } catch (IOException e) {
            System.out.println("supplierClient will be finished, server seems to be stopped");
        }
        System.out.println("sentCount=" + sentCount + "\treceiveCount=" + receiveCount);
        client.stop();
        System.out.println("supplierClient finished");
    }
}
