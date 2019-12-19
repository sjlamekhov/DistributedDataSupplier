package distributeddatasupplier.server.network.messageTransceiver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NetworkTransceiver implements Transceiver {

    private static Logger logger = LogManager.getLogger(NetworkTransceiver.class);

    public void sendMessage(SelectionKey key, String message)
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer myBuffer=ByteBuffer.allocate(1024);
        myBuffer.put(message.getBytes());
        myBuffer.flip();
        int bytesWritten = client.write(myBuffer);
        logger.info(String.format("bytesWritten:\t%s", bytesWritten));
    }

    public String getMessage(SelectionKey key)
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer myBuffer = ByteBuffer.allocate(1024);
        client.read(myBuffer);
        return new String(myBuffer.array()).trim();
    }

}
