package distributeddatasupplier.server.network.messageTransceiver;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface Transceiver {

    void sendMessage(SelectionKey key, String message) throws IOException;
    String getMessage(SelectionKey key) throws IOException;
}
