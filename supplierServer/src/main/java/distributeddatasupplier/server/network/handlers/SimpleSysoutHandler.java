package distributeddatasupplier.server.network.handlers;

import distributeddatasupplier.server.network.SelectorUtils;
import tasks.Task;
import tasks.marshallers.TaskMarshaller;
import distributeddatasupplier.server.suppliers.TaskSupplier;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import static distributeddatasupplier.server.network.MessagingUtils.getMessage;
import static distributeddatasupplier.server.network.MessagingUtils.sendMessage;

public class SimpleSysoutHandler implements Handler {

    private final TaskSupplier taskSupplier;
    private final TaskMarshaller taskMarshaller;

    public SimpleSysoutHandler(TaskSupplier taskSupplier, TaskMarshaller taskMarshaller) {
        this.taskSupplier = taskSupplier;
        this.taskMarshaller = taskMarshaller;
    }

    @Override
    public void handleAcceptable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        SelectorUtils.register(selector, serverSocket, SelectionKey.OP_WRITE);
    }

    @Override
    public void handleReadable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        String message = getMessage(key);
        if (!message.isEmpty()) {
            Task task = taskMarshaller.unmarshallTask(message);
            taskSupplier.markTaskAsFinished(task.getTaskId());
            System.out.println(String.format("from %s:\t%s", selector.hashCode(), task));
            SelectorUtils.prepareForWrite(selector, key);
        }
    }

    @Override
    public void handleWritable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        sendMessage(key, taskMarshaller.mashallTask(taskSupplier.getTask()));
        SelectorUtils.prepareForRead(selector, key);
    }

    @Override
    public boolean readyToHandleWritable() {
        return !taskSupplier.isEmpty();
    }

}