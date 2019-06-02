package distributeddatasupplier.server;

import configuration.ConfigurationService;
import distributeddatasupplier.server.network.handlers.SimpleHandler;

import java.io.IOException;

public class ServerApplication {

    public static void main(String[] args) throws IOException {
        System.out.println("supplierServer started");
        ConfigurationService configurationService = new ConfigurationService();
        System.out.println(String.format("host=%s, port=%s",
                configurationService.getHost(), configurationService.getPort()));
        ServerLoop serverLoop = new ServerLoop(
                configurationService.getHost(), configurationService.getPort(),
                new SimpleHandler()
        );
        serverLoop.start();
        System.out.println("supplierServer finished");
    }

}
