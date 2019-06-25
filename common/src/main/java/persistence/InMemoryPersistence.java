package persistence;

import objects.AbstractObject;
import objects.AbstractObjectUri;

import java.util.*;

public class InMemoryPersistence<U extends AbstractObjectUri, T extends AbstractObject> implements PersistenceLayer<U, T> {

    private final Map<U, T> storage;

    public InMemoryPersistence() {
        this.storage = new HashMap<>();
    }

    @Override
    public boolean isUriPresented(U uri) {
        return storage.containsKey(uri);
    }

    @Override
    public T add(T object) {
        Objects.requireNonNull(object);
        return storage.put((U) object.getUri(), object);
    }

    @Override
    public T update(T object) {
        Objects.requireNonNull(object);
        return storage.put((U) object.getUri(), object);
    }

    @Override
    public T getByUri(U objectUri) {
        return storage.get(objectUri);
    }

    @Override
    public Collection<T> getByUris(Collection<U> objectUris) {
        List<T> result = new ArrayList<>();
        for (U u : objectUris) {
            T t = storage.get(u);
            if (t != null) {
                result.add(storage.get(u));
            }
        }
        return result;
    }

    @Override
    public void deleteObject(U uri) {
        storage.remove(uri);
    }

    @Override
    public Iterator<U> getUriIterator() {
        return storage.keySet().iterator();
    }
}
