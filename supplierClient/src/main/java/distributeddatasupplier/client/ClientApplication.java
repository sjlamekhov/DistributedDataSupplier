package distributeddatasupplier.client;

import configuration.ConfigProvider;
import distributeddatasupplier.client.configuration.ClientConfigurationService;
import distributeddatasupplier.client.processing.CompositeTaskProcessor;
import distributeddatasupplier.client.processing.htmlExtractor.WebExtractorTaskProcessor;
import marshallers.*;
import messaging.FlowControl;
import messaging.Message;
import objects.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class ClientApplication {

    private static Logger logger = LogManager.getLogger(ClientApplication.class);

    public static void main(String[] args) throws IOException {
        logger.info("supplierClient started");

        ClientConfigurationService configurationService = new ClientConfigurationService(
                ConfigProvider.getProperties()
        );

        CompositeTaskProcessor compositeTaskProcessor = new CompositeTaskProcessor();
        compositeTaskProcessor.registerProcessor(TaskType.HTML_EXTRACT, new WebExtractorTaskProcessor());

        Marshaller<Task> taskMarshaller = new IdOnlyTaskMarshaller();
        Marshaller<TaskUri> taskUriMarshaller = new TaskUriMarshaller();
        Marshaller<ResultUri> resultUriMarshaller = new ResultUriMarshaller();
        MessageMarshaller messageMarshaller = new MessageMarshaller(
                taskMarshaller,
                new ResultMarshaller(resultUriMarshaller, taskUriMarshaller)
        );
        Client client = new Client(configurationService.getHost(), configurationService.getPort());
        if (!client.isStarted()) {
            logger.info("supplierClient did not start due to network issues");
            return;
        }
        int attemptsToGetNewTask = configurationService.getNumberOfAttemptsToGetNewTask();
        long newTaskAttemptPause = configurationService.getNewTaskAttemptPause();
        int numberOfCyclesToProcess = configurationService.getNumberOfCyclesToProcess();
        int attemptsCount = 0;
        int receiveCount = 0;
        int sentCount = 0;
        try {
            for (int i = 0; i < numberOfCyclesToProcess; i++) {
                String message = client.getMessage();
                receiveCount++;

                Message messageObject = messageMarshaller.unmarshall(message);

                logger.info("received:\t" + message);
                Task task = messageObject.getTask();
                if (task == null) {
                    logger.info("unexpected message, task == null");
                    client.stop();
                    return;
                } else if (Objects.equals(task.getUri(), Task.EMPTY_TASK.getUri())) {
                    if (attemptsCount < attemptsToGetNewTask) {
                        client.sendMessage(messageMarshaller.marshall(new Message(Result.EMPTY_RESULT, FlowControl.GETNEXTTASK)));
                        sleepBeforeNextAttempt(newTaskAttemptPause);
                        sentCount++;
                        attemptsCount++;
                    } else {
                        client.sendMessage(messageMarshaller.marshall(new Message(FlowControl.BYE)));
                        client.stop();
                        return;
                    }
                } else {
                    Message resultMessage = new Message(
                            compositeTaskProcessor.process(task),
                            i != numberOfCyclesToProcess - 1 ? FlowControl.GETNEXTTASK : FlowControl.LAST
                    );
                    String messageMarshalled = messageMarshaller.marshall(resultMessage);
                    client.sendMessage(messageMarshalled);
                    sentCount++;
                    logger.info("sent:\t" + messageMarshalled);
                }
            }
        } catch (IOException e) {
            logger.info("supplierClient will be finished, server seems to be stopped");
        }
        logger.info("sentCount=" + sentCount + "\treceiveCount=" + receiveCount);
        client.stop();
        logger.info("supplierClient finished");
    }

    private static void sleepBeforeNextAttempt(long newTaskAttemptPause) {
        try {
            Thread.sleep(newTaskAttemptPause);
        } catch (InterruptedException ignore) {}
    }
}
