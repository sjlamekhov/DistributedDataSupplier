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
    private final Set<String> tenants;
    private static final String tenantId = "tenantId";  //stub, to be implemented

    private final Handler handler;

    private final Selector selector;
    private final ServerSocketChannel serverSocket;

    private static final int timeout = 1000;

    public ServerLoop(Handler handler,
                      Selector selector,
                      ServerSocketChannel serverSocket,
                      long maxExecutionTime,
                      Set<String> tenants) {
        this.isStopped = false;
        this.handler = handler;
        this.selector = selector;
        this.serverSocket = serverSocket;
        this.maxExecutionTime = maxExecutionTime;
        this.tenants = tenants;
    }

    public void start() throws IOException {
        long startupTime = System.currentTimeMillis();
        int handledAcceptable = 0, handledReadable = 0, handledWritable = 0;
        long numberOfReceivedRequests = 0;
        while (!isStopped) {
            isExecutionTimeExceeded(startupTime, numberOfReceivedRequests);
            selector.select(timeout);
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {

                SelectionKey key = iter.next();
                iter.remove();

                try {
                    if (key.isAcceptable()) {
                        handler.handleAcceptable(selector, serverSocket, null);
                        handledAcceptable++;
                    } else if (key.isWritable()) {
                        handler.handleWritable(selector, serverSocket, key);
                        handledWritable++;
                    } else if (key.isReadable()) {
                        handler.handleReadable(selector, serverSocket, key);
                        handledReadable++;
                    }
                    numberOfReceivedRequests++;
                    System.out.println(String.format("a:%s, r:%s, w:%s", handledAcceptable, handledReadable, handledWritable));
                } catch (Exception e) {
                    key.channel().close();
                }

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
