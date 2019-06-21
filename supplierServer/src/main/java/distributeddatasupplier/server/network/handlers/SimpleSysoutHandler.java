package distributeddatasupplier.server.network.handlers;

import distributeddatasupplier.server.network.SelectorUtils;
import distributeddatasupplier.server.network.messageTransceiver.Transceiver;
import marshallers.MessageMarshaller;
import messaging.FlowControl;
import messaging.Message;
import objects.Result;
import distributeddatasupplier.server.suppliers.TaskSupplier;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class SimpleSysoutHandler implements Handler {

    private final TaskSupplier taskSupplier;
    private final Transceiver transceiver;
    private final MessageMarshaller messageMarshaller;

    public SimpleSysoutHandler(TaskSupplier taskSupplier, Transceiver transceiver, MessageMarshaller messageMarshaller) {
        this.taskSupplier = taskSupplier;
        this.transceiver = transceiver;
        this.messageMarshaller = messageMarshaller;
    }

    @Override
    public void handleAcceptable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        SelectorUtils.register(selector, serverSocket, SelectionKey.OP_WRITE);
    }

    @Override
    public void handleReadable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
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
                taskSupplier.markTaskAsFinished(result.getTaskUri());
            }
            System.out.println(String.format("from %s:\t%s", selector.hashCode(), messageObject));
            if (messageObject.getFlowControl() == FlowControl.GETNEXTTASK) {
                SelectorUtils.prepareForWrite(selector, key);
            } else if (messageObject.getFlowControl() == FlowControl.LAST) {
                key.cancel();
            }
        }
    }

    @Override
    public void handleWritable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        transceiver.sendMessage(key, messageMarshaller.marshall(new Message(taskSupplier.getTask(), FlowControl.DUMMY)));
        SelectorUtils.prepareForRead(selector, key);
    }

}