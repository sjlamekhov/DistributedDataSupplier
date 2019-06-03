package distributeddatasupplier.client.processing;

public interface TaskProcessor<I,O> {

    O process(I input);

}
