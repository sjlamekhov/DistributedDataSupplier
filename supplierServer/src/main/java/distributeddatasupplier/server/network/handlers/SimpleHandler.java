package distributeddatasupplier.server.network.handlers;

import distributeddatasupplier.server.network.SelectorUtils;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import static distributeddatasupplier.server.network.MessagingUtils.getMessage;
import static distributeddatasupplier.server.network.MessagingUtils.sendMessage;

public class SimpleHandler implements Handler {

    @Override
    public void handleAcceptable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        SelectorUtils.register(selector, serverSocket, SelectionKey.OP_WRITE);
    }

    @Override
    public void handleReadable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        String message = getMessage(key);
        if (!message.isEmpty()) {
            System.out.println(String.format("from %s:\t%s", selector.hashCode(), message));
            SelectorUtils.prepareForWrite(selector, key);
        }
    }

    @Override
    public void handleWritable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
        sendMessage(key, "messageFromServer" + key);
        SelectorUtils.prepareForRead(selector, key);
    }
}