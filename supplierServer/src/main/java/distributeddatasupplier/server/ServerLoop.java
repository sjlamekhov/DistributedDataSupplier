package distributeddatasupplier.server;

import distributeddatasupplier.server.network.handlers.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerLoop {

    private static final long REQUEST_COUNT_LIMIT = 64;

    private final String host;
    private final int port;
    private boolean isStopped;

    private final Handler handler;

    public ServerLoop(String host, int port,
                      Handler handler) {
        this.host = host;
        this.port = port;
        this.isStopped = false;
        this.handler = handler;
    }

    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(host, port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        long numberOfReceivedRequests = 0;

        while (!isStopped) {
            if (numberOfReceivedRequests >= REQUEST_COUNT_LIMIT) {
                System.out.println(String.format("limit of requests is reached:\t%s", numberOfReceivedRequests));
                stop();
            }
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                try {
                    if (key.isAcceptable()) {
                        handler.handleAcceptable(selector, serverSocket, null);
                    } else if (key.isReadable()) {
                        handler.handleReadable(selector, serverSocket, key);
                    } else if (key.isWritable()) {
                        if (handler.readyToHandleWritable()) {
                            handler.handleWritable(selector, serverSocket, key);
                        }
                    }
                    numberOfReceivedRequests++;
                } catch (Exception e) {
                    key.channel().close();
                }

                iter.remove();
            }
        }
    }

    private void stop() {
        isStopped = true;
    }

}
