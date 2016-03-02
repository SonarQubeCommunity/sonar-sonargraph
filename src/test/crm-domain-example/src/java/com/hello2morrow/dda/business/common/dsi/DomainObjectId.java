package com.hello2morrow.dda.business.common.dsi;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;

public class DomainObjectId implements ObjectIdIf
{
    protected final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final Class m_DataSupplierInterfaceClass;
    private final int m_HashCode;

    public DomainObjectId(Class dataSupplierInterfaceClass)
    {
        assert dataSupplierInterfaceClass != null;
        m_DataSupplierInterfaceClass = dataSupplierInterfaceClass;
        m_HashCode = m_DataSupplierInterfaceClass.hashCode();
    }

    public final Class getDataSupplierInterfaceClass()
    {
        return m_DataSupplierInterfaceClass;
    }

    protected boolean equalsIdSpecialization(DomainObjectId id)
    {
        assert id != null;
        return id == this;
    }

    protected int hashCodeSpecialization()
    {
        return 0;
    }

    public final boolean equals(Object obj)
    {
        boolean equals = false;

        if (obj == null)
        {
            equals = false;
        }
        else if (obj == this)
        {
            equals = true;
        }
        else if (obj instanceof DomainObjectId)
        {
            DomainObjectId id = (DomainObjectId) obj;
            Class compareToInterfaceClass = id.getDataSupplierInterfaceClass();
            equals = getDataSupplierInterfaceClass().equals(compareToInterfaceClass) && equalsIdSpecialization(id);
        }

        return equals;
    }

    public final int hashCode()
    {
        return m_HashCode + hashCodeSpecialization();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(getClass().getName());
        buffer.append(LINE_SEPARATOR);
        buffer.append("data supplier interface name = ");
        buffer.append(m_DataSupplierInterfaceClass.getName());
        return buffer.toString();
    }
}