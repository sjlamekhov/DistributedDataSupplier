package persistence.converters;

import objects.AbstractObject;

public interface ObjectConverter<T extends AbstractObject, TO> {
    T buildObjectFromTO(String tenantId, TO transferObject);
    TO buildToFromObject(String tenantId, T object);
}