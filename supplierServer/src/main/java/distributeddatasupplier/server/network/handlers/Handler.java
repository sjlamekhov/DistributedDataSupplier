package distributeddatasupplier.server.network.handlers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public interface Handler {

    void handleAcceptable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException;
    void handleReadable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException;
    void handleWritable(Selector selector, ServerSocketChannel serverSocket, SelectionKey key) throws IOException;

}
