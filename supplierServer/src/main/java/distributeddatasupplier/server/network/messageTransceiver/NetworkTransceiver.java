package distributeddatasupplier.server.network.messageTransceiver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NetworkTransceiver implements Transceiver {

    public void sendMessage(SelectionKey key, String message)
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer myBuffer=ByteBuffer.allocate(1024);
        myBuffer.put(message.getBytes());
        myBuffer.flip();
        int bytesWritten = client.write(myBuffer);
        System.out.println(String.format("bytesWritten:\t%s", bytesWritten));
    }

    public String getMessage(SelectionKey key)
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer myBuffer = ByteBuffer.allocate(1024);
        client.read(myBuffer);
        return new String(myBuffer.array()).trim();
    }

}
