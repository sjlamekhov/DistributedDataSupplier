package distributeddatasupplier.server.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SelectorUtils {

    public static void register(Selector selector, ServerSocketChannel serverSocket, int ops)
            throws IOException {
        SocketChannel socketChannel = serverSocket.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, ops);
    }

    private static void prepareFor(Selector selector, SelectionKey selectionKey, int ops)
            throws IOException {
        selectionKey.channel().register(selector, ops);
    }

    public static void prepareForRead(Selector selector, SelectionKey selectionKey)
            throws IOException {
        prepareFor(selector, selectionKey, SelectionKey.OP_READ);
    }

    public static void prepareForWrite(Selector selector, SelectionKey selectionKey)
            throws IOException {
        prepareFor(selector, selectionKey, SelectionKey.OP_WRITE);
    }

}
