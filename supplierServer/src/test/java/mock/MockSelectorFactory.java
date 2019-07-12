package mock;

import distributeddatasupplier.server.network.selectorfactory.SelectorFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.AbstractSelector;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MockSelectorFactory implements SelectorFactory {

    @Override
    public Selector getSelector() throws IOException {
        AbstractSelector abstractSelector = mock(AbstractSelector.class);
        doReturn(null).when(abstractSelector).keys();
        Set<SelectionKey> keys = new HashSet<>(Arrays.asList(
                getAcceptableKey(), getWritableKey(), getReadableKey()
        ));
        //---------
        doReturn(keys).when(abstractSelector).selectedKeys();
        doReturn(0).when(abstractSelector).selectNow();
        doReturn(0).when(abstractSelector).select(any(Integer.class));
        doReturn(0).when(abstractSelector).select();
        doReturn(null).when(abstractSelector).wakeup();
        return abstractSelector;
    }

    public static SelectionKey getAcceptableKey() {
        SelectionKey acceptableKey = mock(SelectionKey.class);
        doReturn(SelectionKey.OP_ACCEPT).when(acceptableKey).readyOps();
        doReturn(true).when(acceptableKey).isAcceptable();
        doReturn(false).when(acceptableKey).isReadable();
        doReturn(false).when(acceptableKey).isWritable();
        return acceptableKey;
    }

    public static SelectionKey getWritableKey() throws IOException {
        SelectionKey writableKey = mock(SelectionKey.class);
        doReturn(SelectionKey.OP_WRITE).when(writableKey).readyOps();
        doReturn(true).when(writableKey).isWritable();
        doReturn(false).when(writableKey).isAcceptable();
        doReturn(false).when(writableKey).isReadable();
        SocketChannel socketChannel = mock(SocketChannel.class);
        doReturn(0).when(socketChannel).write(any(ByteBuffer.class));
        doReturn(socketChannel).when(writableKey).channel();
        return writableKey;
    }

    public static SelectionKey getReadableKey() throws IOException {
        SelectionKey readableKey = mock(SelectionKey.class);
        doReturn(SelectionKey.OP_READ).when(readableKey).readyOps();
        doReturn(true).when(readableKey).isReadable();
        doReturn(false).when(readableKey).isWritable();
        doReturn(false).when(readableKey).isAcceptable();
        SocketChannel socketChannel = mock(SocketChannel.class);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((ByteBuffer)args[0]).put("MOCKEDCLIENTRESPONSE".getBytes());
            return 0;
        }).when(socketChannel).read(any(ByteBuffer.class));

        doReturn(socketChannel).when(readableKey).channel();
        return readableKey;
    }

    @Override
    public ServerSocketChannel getServerSocket() throws IOException {
        ServerSocketChannel serverSocketChannel = mock(ServerSocketChannel.class);
        doReturn(mock(SocketChannel.class)).when(serverSocketChannel).accept();
        return serverSocketChannel;
    }
}
