package dao;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import persistence.PersistenceLayer;

import java.util.Collection;

public abstract class AbstractDao<U extends AbstractObjectUri, T extends AbstractObject> {

    protected final PersistenceLayer<U, T> persistenceLayer;

    public AbstractDao(PersistenceLayer<U, T> persistenceLayer) {
        this.persistenceLayer = persistenceLayer;
    }

    public boolean isUriPresented(U uri) {
        return persistenceLayer.isUriPresented(uri);
    }

    public T add(T object) {
        object.setCreateTimestamp(System.currentTimeMillis());
        return (T) persistenceLayer.add(object);
    }

    public T update(T object) {
        object.setUpdateTimestamp(System.currentTimeMillis());
        return (T) persistenceLayer.update(object);
    }

    public T getByUri(U uri) {
        return (T) persistenceLayer.getByUri(uri);
    }

    public Collection<T> getByUris(Collection<U> uris) {
        return (Collection<T>) persistenceLayer.getByUris(uris);
    }

    public void deleteObject(U uri) {
        persistenceLayer.deleteObject(uri);
    }

}
