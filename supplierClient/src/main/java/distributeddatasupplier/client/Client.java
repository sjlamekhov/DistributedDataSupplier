package distributeddatasupplier.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    private SocketChannel client;
    private ByteBuffer buffer;

    public void stop() throws IOException {
        client.close();
        buffer = null;
    }

    public Client(String host, int port) {
        try {
            client = SocketChannel.open(new InetSocketAddress(host, port));
            buffer = ByteBuffer.allocate(256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes());
        try {
            client.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String getMessage() throws IOException {
        buffer = ByteBuffer.allocate(1024);
        client.read(buffer);
        return new String(buffer.array()).trim();
    }
}
