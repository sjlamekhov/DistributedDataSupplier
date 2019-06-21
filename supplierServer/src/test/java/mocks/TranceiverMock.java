package mocks;

import distributeddatasupplier.server.network.messageTransceiver.Transceiver;
import marshallers.MessageMarshaller;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.Queue;

public class TranceiverMock implements Transceiver {

    private final MessageMarshaller messageMarshaller;
    private final Queue<String> sendedMessages;
    private final Queue<String> messagesToReceive;

    public TranceiverMock(MessageMarshaller messageMarshaller,
                          Queue<String> sendedMessages,
                          ArrayDeque<String> messagesToReceive) {
        this.messageMarshaller = messageMarshaller;
        this.sendedMessages = sendedMessages;
        this.messagesToReceive = messagesToReceive;
    }

    @Override
    public void sendMessage(SelectionKey key, String message) throws IOException {
        sendedMessages.add(message);
    }

    @Override
    public String getMessage(SelectionKey key) throws IOException {
        return messagesToReceive.poll();
    }


}
