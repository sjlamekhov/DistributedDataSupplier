package distributeddatasupplier.client;

import configuration.ConfigurationService;
import distributeddatasupplier.client.processing.AppenderTaskProcessor;
import distributeddatasupplier.client.processing.TaskProcessor;
import tasks.Task;
import tasks.marshallers.IdOnlyTaskMarshaller;
import tasks.marshallers.TaskMarshaller;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ClientApplication {
    public static void main(String[] args) throws IOException {
        System.out.println("supplierClient started");

        ConfigurationService configurationService = new ConfigurationService();
        Client client = new Client(configurationService.getHost(), configurationService.getPort());
        TaskProcessor<Task, String> taskProcessor = new AppenderTaskProcessor();
        TaskMarshaller taskMarshaller = new IdOnlyTaskMarshaller();
        try {
            for (int i = 0; i < 8; i++) {
                String message = client.getMessage();
                System.out.println("received:\t" + message);
                Task task = taskMarshaller.unmarshallTask(message);
                if (Objects.equals(task.getTaskId(), Task.EMPTY_TASK.getTaskId())) {
                    return;
                }
                try { Thread.sleep(10000); } catch (Exception e) {}
                String processed = taskProcessor.process(task);
                client.sendMessage(processed);
                System.out.println("sent:\t" + processed);
            }
        } catch (IOException e) {
            System.out.println("supplierClient will be finished, server seems to be stopped");
        }

        client.stop();
        System.out.println("supplierClient finished");
    }
}
