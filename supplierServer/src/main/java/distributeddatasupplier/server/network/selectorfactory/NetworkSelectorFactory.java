package distributeddatasupplier.server.network.selectorfactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class NetworkSelectorFactory implements SelectorFactory {

    private final Selector selector;
    private final ServerSocketChannel serverSocket;

    public NetworkSelectorFactory(String host, int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(host, port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public Selector getSelector() {
        return selector;
    }

    @Override
    public ServerSocketChannel getServerSocket() {
        return serverSocket;
    }
}
