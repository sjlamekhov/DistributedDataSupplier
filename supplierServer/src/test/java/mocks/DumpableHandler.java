package mocks;

import distributeddatasupplier.server.network.SelectorUtils;
import distributeddatasupplier.server.network.handlers.Handler;
import distributeddatasupplier.server.network.messageTransceiver.NetworkTransceiver;
import distributeddatasupplier.server.network.messageTransceiver.Transceiver;
import distributeddatasupplier.server.suppliers.TaskSupplier;
import marshallers.MessageMarshaller;
import messaging.FlowControl;
import messaging.Message;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DumpableHandler implements Handler {

    private final TaskSupplier taskSupplier;
    private final Transceiver transceiver;
    private final MessageMarshaller messageMarshaller;
    private final List<String> messagesFromServer;
    private final List<String> messagesFromClient;
    private final String tenantId;

    public DumpableHandler(String tenantId, TaskSupplier taskSupplier, MessageMarshaller messageMarshaller) {
        this.tenantId = tenantId;
        this.taskSupplier = taskSupplier;
        this.transceiver = new NetworkTransceiver();
        this.messageMarshaller = messageMarshaller;
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
        String message = transceiver.getMessage(key);
        if (!message.isEmpty()) {
            messagesFromClient.add(message);
            System.out.println(String.format("from %s:\t%s", selector.hashCode(), message));
            SelectorUtils.prepareForWrite(selector, key);
        }
    }

    @Override
    public void handleWritable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        String message = messageMarshaller.marshall(new Message(taskSupplier.pollTask(tenantId), FlowControl.DUMMY));
        transceiver.sendMessage(key, message);
        messagesFromServer.add(message);
        SelectorUtils.prepareForRead(selector, key);
    }

}
