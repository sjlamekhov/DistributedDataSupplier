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

    private boolean isStopped;
    private final long maxExecutionTime;

    private final Handler handler;

    private final Selector selector;
    private final ServerSocketChannel serverSocket;

    public ServerLoop(Handler handler,
                      Selector selector,
                      ServerSocketChannel serverSocket,
                      long maxExecutionTime) {
        this.isStopped = false;
        this.handler = handler;
        this.selector = selector;
        this.serverSocket = serverSocket;
        this.maxExecutionTime = maxExecutionTime;
    }

    public void start() throws IOException {
        long startupTime = System.currentTimeMillis();

        long numberOfReceivedRequests = 0;
        while (!isStopped) {
            isExecutionTimeExceeded(startupTime, numberOfReceivedRequests);
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                try {
                    if (key.isAcceptable()) {
                        handler.handleAcceptable(selector, serverSocket, null);
                    } else if (key.isWritable()) {
                        if (handler.readyToHandleWritable()) {
                            handler.handleWritable(selector, serverSocket, key);
                        }
                    } else if (key.isReadable()) {
                        handler.handleReadable(selector, serverSocket, key);
                    }
                    numberOfReceivedRequests++;
                } catch (Exception e) {
                    key.channel().close();
                }

                iter.remove();
            }
        }
    }

    private void isExecutionTimeExceeded(long startupTime, long numberOfReceivedRequests) {
        if (maxExecutionTime != -1 && (System.currentTimeMillis() - startupTime) >= maxExecutionTime) {
            System.out.println(String.format("limit of executionTime is reached, numberOfReceivedRequests:\t%s",
                    numberOfReceivedRequests));
            stop();
        }
    }

    public void stop() {
        isStopped = true;
    }

}
