package dao;

import objects.AbstractObject;
import objects.AbstractObjectUri;

import java.util.*;

public class CompositeDao<U extends AbstractObjectUri, T extends AbstractObject> extends AbstractDao<U,T> {

    protected final Map<String, AbstractDao<U,T>> daoMapping;

    public CompositeDao() {
        super(null);
        this.daoMapping = new HashMap<>();
    }

    public AbstractDao<U, T> getDao(String tenantId) {
        return daoMapping.get(tenantId);
    }

    public void addDao(String tenantId, AbstractDao<U, T> dao) {
        if (daoMapping.containsKey(tenantId)) {
            throw new RuntimeException(String.format("DAO for %s tenant is already registered", tenantId));
        }
        daoMapping.put(tenantId, dao);
    }

    @Override
    public boolean isUriPresented(U uri) {
        String tenantId = uri.getTenantId();
        return getDaoInternal(tenantId).isUriPresented(uri);
    }

    @Override
    public T add(T object) {
        String tenantId = object.getUri().getTenantId();
        return getDaoInternal(tenantId).add(object);
    }

    @Override
    public T update(T object) {
        String tenantId = object.getUri().getTenantId();
        return getDaoInternal(tenantId).update(object);
    }

    @Override
    public T getByUri(U uri) {
        String tenantId = uri.getTenantId();
        return getDaoInternal(tenantId).getByUri(uri);
    }

    @Override
    public Collection<T> getByUris(Collection<U> documentUris) {
        Objects.requireNonNull(documentUris);
        if (documentUris.isEmpty()) {
            return Collections.emptySet();
        }
        String tenantId = documentUris.iterator().next().getTenantId();
        return getDaoInternal(tenantId).getByUris(documentUris);
    }

    @Override
    public void deleteObject(U uri) {
        Objects.requireNonNull(uri);
        String tenantId = uri.getTenantId();
        getDaoInternal(tenantId).deleteObject(uri);
    }

    private AbstractDao<U, T> getDaoInternal(String tenantId) {
        if (!daoMapping.containsKey(tenantId)) {
            throw new RuntimeException(String.format("DAO for tenant %s is not registered", tenantId));
        }
        AbstractDao<U, T> dao = daoMapping.get(tenantId);
        return dao;
    }
}
