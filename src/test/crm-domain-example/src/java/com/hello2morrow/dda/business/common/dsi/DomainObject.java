package com.hello2morrow.dda.business.common.dsi;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;


/**
 * Base class for domain objects.
 */
public abstract class DomainObject implements DomainObjectIf
{
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static Logger s_Logger = Logger.getLogger(DomainObject.class);
    private DataSupplierIf m_DataSupplier;
    private int m_HashCode;
    private boolean m_IsMarkedDeleted;

    /**
     * User for creation from subclasses
     * @param dataSupplier
     */
    public DomainObject(DataSupplierIf dataSupplier)
    {
        assert dataSupplier != null;
        m_DataSupplier = dataSupplier;
        DomainObjectId objectId = (DomainObjectId) m_DataSupplier.getObjectId();
        m_HashCode = objectId.hashCode();
        m_DataSupplier.usedByDomainObject(this);
        DomainObjectFactory.getInstance().addToCache(this, objectId);
    }

    public final ObjectIdIf getObjectId()
    {
        assert !hasBeenDeleted();
        return m_DataSupplier.getObjectId();
    }

    public final void updateObjectId(ObjectIdIf objectId)
    {
        assert !hasBeenDeleted();
        m_DataSupplier.updateObjectId(objectId);
    }

    public abstract boolean hasDataSupplier();

    public abstract void delete() throws TechnicalException;

    public final DataSupplierIf getDataSupplier()
    {
        assert !hasBeenDeleted();
        assert hasDataSupplier();
        return m_DataSupplier;
    }

    protected final void setDataSupplier(DataSupplierIf newDataSupplier)
    {
        assert !hasBeenDeleted();
        assert newDataSupplier != null;
        assert m_DataSupplier != null;
        DomainObjectId currentObjectId = (DomainObjectId) ((TransientDataSupplier) m_DataSupplier).getObjectId();
        DomainObjectFactory.getInstance().removeFromCache(currentObjectId);
        m_DataSupplier = newDataSupplier;
        DomainObjectId newObjectId = (DomainObjectId) m_DataSupplier.getObjectId();
        DomainObjectFactory.getInstance().addToCache(this, newObjectId);
        m_HashCode = newObjectId.hashCode();
    }

    public final boolean hasBeenDeleted()
    {
        return m_IsMarkedDeleted;
    }

    /**
     * Callback for the associated data supplier. If the associated data
     * supplier gets deleted the corresponding domain object is marked and
     * removed from the cache.
     */
    public final void markDeleted(DomainObjectId aId)
    {
        assert !hasBeenDeleted();
        DomainObjectFactory.getInstance().removeFromCache(aId);
        m_DataSupplier = null;
        m_IsMarkedDeleted = true;
    }

    /**
     * Two domain objects are equal if the object ids are equal.
     */
    public final boolean equals(Object aObject)
    {
        assert !hasBeenDeleted();
        boolean equals = false;

        if (aObject == null)
        {
            equals = false;
        }
        else if (aObject == this)
        {
            equals = true;
        }
        else if (aObject instanceof DomainObject)
        {
            equals = ((DomainObject) aObject).getObjectId().equals(this.getObjectId());
        }

        return equals;
    }

    public final int hashCode()
    {
        assert !hasBeenDeleted();
        return m_HashCode;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("### domain object class = ");
        buffer.append(getClass().getName());
        buffer.append(" ###");
        buffer.append(LINE_SEPARATOR);
        buffer.append(m_IsMarkedDeleted ? "- deleted" : "- not deleted");
        return buffer.toString();
    }
}