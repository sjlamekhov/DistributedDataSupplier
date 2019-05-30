package distributeddatasupplier.server;

import distributeddatasupplier.server.network.SelectorUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerLoop {

    private static final long REQUEST_COUNT_LIMIT = 64;

    private final String host;
    private final int port;
    private boolean isStopped;

    public ServerLoop(String host, int port) {
        this.host = host;
        this.port = port;
        this.isStopped = false;
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
                        handleAcceptable(selector, serverSocket);
                    } else if (key.isReadable()) {
                        handleReadable(selector, key);
                    } else if (key.isWritable()) {
                        handleWritable(selector, key);
                    }
                    numberOfReceivedRequests++;
                } catch (Exception e) {
                    key.channel().close();
                }

                iter.remove();
            }
        }
    }

    private void handleAcceptable(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SelectorUtils.register(selector, serverSocket, SelectionKey.OP_WRITE);
    }

    private void handleWritable(Selector selector, SelectionKey key) throws IOException {
        sendMessage("messageFromServer" + key, key);
        SelectorUtils.prepareForRead(selector, key);
    }

    private void handleReadable(Selector selector, SelectionKey key) throws IOException {
        String message = getMessage(key);
        if (!message.isEmpty()) {
            System.out.println(String.format("from %s:\t%s", selector.hashCode(), message));
            SelectorUtils.prepareForWrite(selector, key);
        }
    }

    private void stop() {
        isStopped = true;
    }

    private void sendMessage(String message, SelectionKey key)
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer myBuffer=ByteBuffer.allocate(1024);
        myBuffer.put(message.getBytes());
        myBuffer.flip();
        int bytesWritten = client.write(myBuffer);
        System.out.println(String.format("bytesWritten:\t%s", bytesWritten));
    }

    private String getMessage(SelectionKey key)
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer myBuffer = ByteBuffer.allocate(1024);
        client.read(myBuffer);
        return new String(myBuffer.array()).trim();
    }

}
