package distributeddatasupplier.client.processing;

public interface MessageProcessor<I,O> {

    O process(I input);

}
