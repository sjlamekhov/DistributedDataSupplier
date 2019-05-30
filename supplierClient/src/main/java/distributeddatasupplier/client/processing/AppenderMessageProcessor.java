package distributeddatasupplier.client.processing;

public class AppenderMessageProcessor implements MessageProcessor<String, String> {

    @Override
    public String process(String input) {
        return input + "_" + input.hashCode();
    }

}
