package persistence.converters;

import objects.AbstractObject;

public interface ObjectConverter<T extends AbstractObject, TO> {
    T buildObjectFromTO(TO transferObject);
    TO buildToFromObject(T object);
}