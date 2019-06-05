package distributeddatasupplier.server.network.selectorfactory;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public interface SelectorFactory {

    Selector getSelector() throws IOException;
    ServerSocketChannel getServerSocket() throws IOException;

}
