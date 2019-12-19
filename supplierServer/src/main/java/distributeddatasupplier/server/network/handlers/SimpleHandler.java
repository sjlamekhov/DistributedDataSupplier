package distributeddatasupplier.server.network.handlers;

import distributeddatasupplier.server.network.SelectorUtils;
import distributeddatasupplier.server.network.messageTransceiver.Transceiver;
import distributeddatasupplier.server.services.ResultService;
import distributeddatasupplier.server.services.status.ServerStatusService;
import marshallers.Marshaller;
import messaging.FlowControl;
import messaging.Message;
import objects.Result;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import objects.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class SimpleHandler implements Handler {

    private static Logger logger = LogManager.getLogger(SimpleHandler.class);

    private final Set<String> tenantIds;
    private final TaskSupplier taskSupplier;
    private final ResultService resultService;
    private final Consumer<Result> resultConsumer;
    private final Transceiver transceiver;
    private final Marshaller<Message> messageMarshaller;
    private final ServerStatusService serverStatusService;
    private AtomicLong numberOfClients = new AtomicLong(0L);
    private AtomicLong numberOfProcessedTasks = new AtomicLong(0L);

    public SimpleHandler(Set<String> tenantIds,
                         TaskSupplier taskSupplier,
                         ResultService resultService,
                         Consumer<Result> resultConsumer,
                         Transceiver transceiver,
                         Marshaller<Message> messageMarshaller,
                         ServerStatusService serverStatusService) {
        this.tenantIds = tenantIds;
        this.taskSupplier = taskSupplier;
        this.resultService = resultService;
        this.resultConsumer = resultConsumer;
        this.transceiver = transceiver;
        this.messageMarshaller = messageMarshaller;
        this.serverStatusService = serverStatusService;
    }

    public SimpleHandler(Set<String> tenantIds,
                         TaskSupplier taskSupplier,
                         ResultService resultService,
                         Transceiver transceiver,
                         Marshaller<Message> messageMarshaller,
                         ServerStatusService serverStatusService) {
        this(tenantIds, taskSupplier, resultService,
                r -> {},
                transceiver, messageMarshaller, serverStatusService);
    }

    @Override
    public void handleAcceptable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        serverStatusService.incrementNumberOfAcceptRequests();
        SelectorUtils.register(selector, serverSocket, SelectionKey.OP_WRITE);
        serverStatusService.setNumberOfAliveClients(numberOfClients.incrementAndGet());
    }

    @Override
    public void handleReadable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        serverStatusService.incrementNumberOfReadRequests();
        String message = transceiver.getMessage(key);
        if (!message.isEmpty()) {
            Message messageObject = messageMarshaller.unmarshall(message);
            Result result = messageObject.getResult();
            if (result == null) {
                key.cancel();
                if (messageObject.getFlowControl() != FlowControl.BYE) {
                    logger.info("unexpected message, result == null, message " + message);
                    return;
                }
            } else if (!Objects.equals(Result.EMPTY_RESULT, result)) {
                resultService.add(result);
                resultConsumer.accept(result);
                taskSupplier.markTaskAsFinished(result.getTaskUri());
                serverStatusService.setNumberOfProcessedTasks(numberOfProcessedTasks.incrementAndGet());
            }
            logger.info(String.format("from %s:\t%s", selector.hashCode(), messageObject));
            if (messageObject.getFlowControl() == FlowControl.GETNEXTTASK) {
                SelectorUtils.prepareForWrite(selector, key);
            } else if (messageObject.getFlowControl() == FlowControl.LAST) {
                serverStatusService.setNumberOfAliveClients(numberOfClients.decrementAndGet());
                key.cancel();
            }
        }
    }

    @Override
    public void handleWritable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        serverStatusService.incrementNumberOfWriteRequests();
        Task task = null;
        for (String tenantId : tenantIds) {
            task = taskSupplier.pollTask(tenantId);
            if (!Objects.equals(task, Task.EMPTY_TASK)) {
                break;
            }
        }
        transceiver.sendMessage(key, messageMarshaller.marshall(new Message(task, FlowControl.DUMMY)));
        SelectorUtils.prepareForRead(selector, key);
    }

}