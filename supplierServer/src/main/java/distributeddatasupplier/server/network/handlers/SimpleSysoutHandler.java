package distributeddatasupplier.server.network.handlers;

import distributeddatasupplier.server.network.SelectorUtils;
import objects.Task;
import marshallers.TaskMarshaller;
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
            Task task = taskMarshaller.unmarshallTask(message.split(";")[0]);
            taskSupplier.markTaskAsFinished(task.getUri());
            System.out.println(String.format("from %s:\t%s", selector.hashCode(), task));
            if (message.endsWith(";GETNEXTTASK")) {
                SelectorUtils.prepareForWrite(selector, key);
            } else if (message.endsWith(";LAST")) {
                key.cancel();
            } else if (message.endsWith(";BYE")) {
                key.cancel();
            }
        }
    }

    @Override
    public void handleWritable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        sendMessage(key, taskMarshaller.mashallTask(taskSupplier.getTask()));
        SelectorUtils.prepareForRead(selector, key);
    }

}