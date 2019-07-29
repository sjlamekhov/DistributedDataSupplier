package persistence;

import com.mongodb.BasicDBObject;
import objects.AbstractObject;
import objects.AbstractObjectUri;
import persistence.converters.ObjectConverter;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryPersistence<U extends AbstractObjectUri, T extends AbstractObject> implements PersistenceLayer<U, T> {

    private final Map<U, BasicDBObject> storage;
    private final ObjectConverter<T, BasicDBObject> converter;

    public InMemoryPersistence(ObjectConverter<T, BasicDBObject> converter) {
        this.storage = new HashMap<>();
        this.converter = converter;
    }

    @Override
    public boolean isUriPresented(U uri) {
        return storage.containsKey(uri);
    }

    @Override
    public T add(T object) {
        Objects.requireNonNull(object);
        storage.put((U) object.getUri(), converter.buildToFromObject(object));
        return object;
    }

    @Override
    public T update(T object) {
        Objects.requireNonNull(object);
        storage.put((U) object.getUri(), converter.buildToFromObject(object));
        return object;
    }

    @Override
    public T getByUri(U objectUri) {
        BasicDBObject dbObject = storage.get(objectUri);
        if (dbObject == null) {
            return null;
        }
        return converter.buildObjectFromTO(dbObject);
    }

    @Override
    public Collection<T> getByUris(Collection<U> objectUris) {
        List<T> result = new ArrayList<>();
        for (U u : objectUris) {
            BasicDBObject dbObject = storage.get(u);
            if (dbObject != null) {
                result.add(converter.buildObjectFromTO(dbObject));
            }
        }
        return result;
    }

    @Override
    public void deleteObject(U uri) {
        storage.remove(uri);
    }

    @Override
    public Collection<U> getObjectUris(int responseSizeLimit) {
        return storage.keySet().stream().limit(responseSizeLimit).collect(Collectors.toSet());
    }

}
