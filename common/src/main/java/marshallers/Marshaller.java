package marshallers;

public interface Marshaller<T> {

    String marshall(T object);
    T unmarshall(String string);

}
