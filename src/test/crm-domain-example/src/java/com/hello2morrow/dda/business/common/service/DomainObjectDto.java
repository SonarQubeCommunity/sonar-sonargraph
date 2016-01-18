package com.hello2morrow.dda.business.common.service;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;

public abstract class DomainObjectDto extends Dto
{
    private ObjectIdIf m_ObjectId;

    public final boolean hasObjectId()
    {
        return m_ObjectId != null;
    }

    public final ObjectIdIf getObjectId()
    {
        assert hasObjectId();
        return m_ObjectId;
    }

    public void setObjectId(ObjectIdIf objectId)
    {
        m_ObjectId = objectId;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);
        if (hasObjectId())
        {
            buffer.append(m_ObjectId);
        }
        else
        {
            buffer.append("- no object id");
        }
        return buffer.toString();
    }
}