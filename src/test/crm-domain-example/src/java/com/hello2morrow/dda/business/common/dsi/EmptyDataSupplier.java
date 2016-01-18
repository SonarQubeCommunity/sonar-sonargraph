package com.hello2morrow.dda.business.common.dsi;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;

final class EmptyDataSupplier implements DataSupplierIf
{
    private final DomainObjectId m_ObjectId = new DomainObjectId(EmptyDataSupplier.class);
    private DomainObjectIf m_DomainObject;

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
        assert m_DomainObject == null;
        m_DomainObject = domainObject;
    }

    public boolean supportsPersistentData()
    {
        return false;
    }

    public DomainObjectIf getDomainObject()
    {
        assert m_DomainObject != null;
        return m_DomainObject;
    }
}