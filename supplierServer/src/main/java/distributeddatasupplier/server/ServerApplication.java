package distributeddatasupplier.server;

import java.io.IOException;

public class ServerApplication {

    public static void main(String[] args) throws IOException {
        System.out.println("supplierServer started");
        ServerLoop serverLoop = new ServerLoop("localhost", 5050);
        serverLoop.start();
        System.out.println("supplierServer finished");
    }

}
