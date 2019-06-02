package distributeddatasupplier.server;

import distributeddatasupplier.server.network.handlers.SimpleHandler;

import java.io.IOException;

public class ServerApplication {

    public static void main(String[] args) throws IOException {
        System.out.println("supplierServer started");
        ServerLoop serverLoop = new ServerLoop("localhost",
                                                    5050,
                                                    new SimpleHandler());
        serverLoop.start();
        System.out.println("supplierServer finished");
    }

}
