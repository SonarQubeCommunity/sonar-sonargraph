package com.hello2morrow.dda.business.common.data.cmp;

import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;

public abstract class BaseDataSupplierAdapter implements DataSupplierIf
{
    private final DataSupplierIf m_RealSupplier;
    private final Class m_DataSupplierClass;
    private ObjectIdIf m_ObjectId;
    private DomainObjectIf m_DomainObject;

    protected BaseDataSupplierAdapter(DataSupplierIf realSupplier, Class dataSupplierClass)
    {
        assert realSupplier != null;
        assert dataSupplierClass != null;
        m_RealSupplier = realSupplier;
        m_DataSupplierClass = dataSupplierClass;
    }

    public final ObjectIdIf getObjectId()
    {
        if (m_ObjectId == null)
        {
            assert m_RealSupplier.getObjectId() instanceof CmpObjectId;
            CmpObjectId realSupplierId = (CmpObjectId) m_RealSupplier.getObjectId();
            m_ObjectId = new CmpObjectId(m_DataSupplierClass, realSupplierId.getId(), realSupplierId.getVersion());
        }
        return m_ObjectId;
    }

    public final void usedByDomainObject(DomainObjectIf domainObject)
    {
        assert domainObject != null;
        m_DomainObject = domainObject;
    }

    public final DomainObjectIf getDomainObject()
    {
        assert m_DomainObject != null;
        return m_DomainObject;
    }

    public final boolean supportsPersistentData()
    {
        return true;
    }
}