package com.hello2morrow.dda.business.common.dsi;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;

public abstract class TestDataSupplier implements DataSupplierIf
{
    private final DomainObjectId m_ObjectId;
    private DomainObjectIf m_DomainObject;

    protected TestDataSupplier(DomainObjectId id)
    {
        assert id != null;
        m_ObjectId = id;
    }

    public ObjectIdIf getObjectId()
    {
        return m_ObjectId;
    }

    public void updateObjectId(ObjectIdIf objectId)
    {
        assert objectId != null;
        assert m_ObjectId.equals(objectId);
    }

    public void usedByDomainObject(DomainObjectIf domainObject)
    {
        assert domainObject != null;
        m_DomainObject = domainObject;
    }

    public boolean supportsPersistentData()
    {
        return true;
    }

    public DomainObjectIf getDomainObject()
    {
        assert m_DomainObject != null;
        return m_DomainObject;
    }
}