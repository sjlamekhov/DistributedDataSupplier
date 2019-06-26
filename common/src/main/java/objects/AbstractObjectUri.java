package objects;

import dao.UriGenerator;

public abstract class AbstractObjectUri {

    protected final String id;
    protected final String tenantId;

    public AbstractObjectUri(String tenantId) {
        this.id = UriGenerator.generateId();
        this.tenantId = tenantId;
    }

    public AbstractObjectUri(String id, String tenantId) {
        this.id = id;
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractObjectUri that = (AbstractObjectUri) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return id;
    }

}
