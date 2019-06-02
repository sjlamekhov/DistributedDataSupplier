package distributeddatasupplier.client;

import distributeddatasupplier.client.processing.AppenderMessageProcessor;
import distributeddatasupplier.client.processing.MessageProcessor;

import java.io.IOException;

public class ClientApplication {
    public static void main(String[] args) throws IOException {
        System.out.println("supplierClient started");
        Client client = new Client("localhost", 5050);
        MessageProcessor<String, String> messageProcessor = new AppenderMessageProcessor();

        try {
            for (int i = 0; i < 8; i++) {
                String message = client.getMessage();
                System.out.println("received:\t" + message);
                String processed = messageProcessor.process(message);
                client.sendMessage(processed);
                System.out.println("sent:\t" + processed);
            }
        } catch (IOException e) {
            System.out.println("supplierClient will be finished due to exception");
        }

        client.stop();
        System.out.println("supplierClient finished");
    }
}
