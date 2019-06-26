package distributeddatasupplier.server.network.handlers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public interface Handler {

    void handleAcceptable(String tenantId, Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException;
    void handleReadable(String tenantId, Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException;
    void handleWritable(String tenantId, Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException;

}
