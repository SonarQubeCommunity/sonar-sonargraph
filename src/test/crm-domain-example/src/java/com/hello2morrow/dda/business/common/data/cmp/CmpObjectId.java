package com.hello2morrow.dda.business.common.data.cmp;

import java.util.HashMap;
import java.util.Map;

import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public final class CmpObjectId extends DomainObjectId
{
    private final Long m_Id;
    private final int m_HashCode;
    private Long m_Version;
    private Map m_PropertyToVersionMap;

    CmpObjectId(Class dataSupplierInterfaceClass, Long id, Long version)
    {
        super(dataSupplierInterfaceClass);
        assert id != null;
        assert version != null;
        m_Id = id;

        m_HashCode = id.intValue();
        m_Version = version;
    }

    public Long getId()
    {
        return m_Id;
    }

    public Long getVersion()
    {
        assert m_Version != null;
        return m_Version;
    }

    public void setVersion(Long version)
    {
        assert version != null;
        m_Version = version;
    }

    void clearNToMVersions()
    {
        if (m_PropertyToVersionMap != null)
        {
            m_PropertyToVersionMap.clear();
        }
    }

    public void addNToMVersion(String property, Object nToMPk, Long version)
    {
        assert property != null;
        assert property.length() > 0;
        assert nToMPk != null;
        assert version != null;

        Map pkToVersion = null;

        if (m_PropertyToVersionMap == null)
        {
            m_PropertyToVersionMap = new HashMap();
            pkToVersion = new HashMap();
            m_PropertyToVersionMap.put(property, pkToVersion);
        }
        else
        {
            pkToVersion = (Map) m_PropertyToVersionMap.get(property);
            if (pkToVersion == null)
            {
                pkToVersion = new HashMap();
                m_PropertyToVersionMap.put(property, pkToVersion);
            }
        }

        assert !pkToVersion.containsKey(nToMPk);
        pkToVersion.put(nToMPk, version);
    }

    public Long getNToMVersion(String property, Object nToMPk)
    {
        assert property != null;
        assert property.length() > 0;
        assert nToMPk != null;

        if (m_PropertyToVersionMap != null)
        {
            Map versionMap = (Map) m_PropertyToVersionMap.get(property);
            if (versionMap != null)
            {
                return (Long) versionMap.get(nToMPk);
            }
        }

        return null;
    }

    protected boolean equalsIdSpecialization(DomainObjectId id)
    {
        assert id != null;
        assert id instanceof CmpObjectId;
        CmpObjectId objectId = (CmpObjectId) id;
        return m_Id.equals(objectId.m_Id);
    }

    protected int hashCodeSpecialization()
    {
        return m_HashCode;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);
        buffer.append("id = ");
        buffer.append(m_Id);
        return buffer.toString();
    }
}