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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleHandler implements Handler {

    private final Set<String> tenantIds;
    private final TaskSupplier taskSupplier;
    private final ResultService resultService;
    private final Transceiver transceiver;
    private final Marshaller<Message> messageMarshaller;
    private final ServerStatusService serverStatusService;
    private AtomicLong numberOfClients = new AtomicLong(0L);
    private AtomicLong numberOfProcessedTasks = new AtomicLong(0L);

    public SimpleHandler(Set<String> tenantIds,
                         TaskSupplier taskSupplier,
                         ResultService resultService,
                         Transceiver transceiver,
                         Marshaller<Message> messageMarshaller,
                         ServerStatusService serverStatusService) {
        this.tenantIds = tenantIds;
        this.taskSupplier = taskSupplier;
        this.resultService = resultService;
        this.transceiver = transceiver;
        this.messageMarshaller = messageMarshaller;
        this.serverStatusService = serverStatusService;
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
                    System.out.println("unexpected message, result == null, message " + message);
                    return;
                }
            } else {
                resultService.add(result);
                taskSupplier.markTaskAsFinished(result.getTaskUri());
                serverStatusService.setNumberOfProcessedTasks(numberOfProcessedTasks.incrementAndGet());
            }
            System.out.println(String.format("from %s:\t%s", selector.hashCode(), messageObject));
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