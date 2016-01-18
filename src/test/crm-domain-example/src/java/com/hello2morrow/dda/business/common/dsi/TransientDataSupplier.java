package com.hello2morrow.dda.business.common.dsi;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;


public abstract class TransientDataSupplier implements DataSupplierIf
{
    private final DomainObjectId m_ObjectId;
    private DomainObjectIf m_DomainObject;

    protected TransientDataSupplier(DomainObjectId id)
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

    public final DataSupplierIf save() throws BusinessException, TechnicalException
    {
        DataSupplierIf created = createPersistentDataSupplier();
        mapContentTo(created);
        return created;
    }
    
    protected abstract DataSupplierIf createPersistentDataSupplier()  throws TechnicalException;
    
    protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
    {
        assert persistent != null;
        assert persistent.supportsPersistentData();
    }
}