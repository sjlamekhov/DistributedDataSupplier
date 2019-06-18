package handlers;

import distributeddatasupplier.server.network.SelectorUtils;
import distributeddatasupplier.server.network.handlers.Handler;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import marshallers.TaskMarshaller;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static distributeddatasupplier.server.network.MessagingUtils.getMessage;
import static distributeddatasupplier.server.network.MessagingUtils.sendMessage;

public class DumpableHandler implements Handler {

    private final TaskSupplier taskSupplier;
    private final TaskMarshaller taskMarshaller;
    private final List<String> messagesFromServer;
    private final List<String> messagesFromClient;

    public DumpableHandler(TaskSupplier taskSupplier, TaskMarshaller taskMarshaller) {
        this.taskSupplier = taskSupplier;
        this.taskMarshaller = taskMarshaller;
        this.messagesFromServer = new ArrayList<>();
        this.messagesFromClient = new ArrayList<>();
    }

    public List<String> getMessagesFromServer() {
        return Collections.unmodifiableList(messagesFromServer);
    }

    public List<String> getMessagesFromClient() {
        return Collections.unmodifiableList(messagesFromClient);
    }

    @Override
    public void handleAcceptable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        SelectorUtils.register(selector, serverSocket, SelectionKey.OP_WRITE);
    }

    @Override
    public void handleReadable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        String message = getMessage(key);
        if (!message.isEmpty()) {
            messagesFromClient.add(message);
            System.out.println(String.format("from %s:\t%s", selector.hashCode(), message));
            SelectorUtils.prepareForWrite(selector, key);
        }
    }

    @Override
    public void handleWritable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        String message = taskMarshaller.mashallTask(taskSupplier.getTask());
        sendMessage(key, message);
        messagesFromServer.add(message);
        SelectorUtils.prepareForRead(selector, key);
    }

}
