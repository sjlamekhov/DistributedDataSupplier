package persistence;

import objects.AbstractObject;
import objects.AbstractObjectUri;

import java.util.Collection;

public interface PersistenceLayer<T extends AbstractObject,U extends AbstractObjectUri> {

    boolean isUriPresented(U uri);
    T add(T object);
    T update(T object);
    T getByUri(U objectUri);
    Collection<T> getByUris(Collection<U> objectUris);
    void deleteObject(U uri);

}
