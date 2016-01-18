package com.hello2morrow.dda.business.common.dsi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;

public final class TestDataSupplierInheritanceCache
{
    private static TestDataSupplierInheritanceCache s_Instance;
    private final Map m_ObjectIdToDsiImpl = new HashMap();
    private final Map m_ClassRootToDsiImpls = new HashMap();

    private TestDataSupplierInheritanceCache()
    {
        //make it unaccessible
    }

    public static TestDataSupplierInheritanceCache getInstance()
    {
        if (s_Instance == null)
        {
            s_Instance = new TestDataSupplierInheritanceCache();
        }
        return s_Instance;
    }

    public void add(DataSupplierIf dataSupplier, Class rootClass)
    {
        assert dataSupplier != null;
        assert dataSupplier.supportsPersistentData();
        assert !m_ObjectIdToDsiImpl.containsKey(dataSupplier.getObjectId());
        assert rootClass != null;
        m_ObjectIdToDsiImpl.put(dataSupplier.getObjectId(), dataSupplier);
        List all = (List) m_ClassRootToDsiImpls.get(rootClass);
        if (all == null)
        {
            all = new ArrayList();
            m_ClassRootToDsiImpls.put(rootClass, all);
        }
        assert !all.contains(dataSupplier);
        all.add(dataSupplier);
    }

    public DataSupplierIf[] getAll(Class rootClass, DataSupplierIf[] type)
    {
        assert rootClass != null;
        assert type != null;
        List all = (List) m_ClassRootToDsiImpls.get(rootClass);
        if (all == null)
        {
            all = new ArrayList(0);
        }
        return (DataSupplierIf[]) all.toArray(type);
    }

    public DataSupplierIf findByObjectId(ObjectIdIf id)
    {
        assert id != null;
        assert id instanceof DomainObjectId;
        return (DataSupplierIf) m_ObjectIdToDsiImpl.get(id);
    }

    public boolean contains(ObjectIdIf id)
    {
        assert id != null;
        return m_ObjectIdToDsiImpl.containsKey(id);
    }

    public void delete(DataSupplierIf dataSupplier, Class rootClass)
    {
        assert dataSupplier != null;
        assert rootClass != null;
        assert m_ObjectIdToDsiImpl.containsKey(dataSupplier.getObjectId());
        m_ObjectIdToDsiImpl.remove(dataSupplier.getObjectId());
        List all = (List) m_ClassRootToDsiImpls.get(rootClass);
        assert all != null;
        assert all.contains(dataSupplier);
        all.remove(dataSupplier);
    }
}