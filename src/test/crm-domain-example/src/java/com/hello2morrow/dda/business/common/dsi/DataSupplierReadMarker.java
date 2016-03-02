package com.hello2morrow.dda.business.common.dsi;

import com.hello2morrow.dda.foundation.common.DateUtil;

public final class DataSupplierReadMarker
{
    private final String m_ReadFromDataSupplier;

    DataSupplierReadMarker()
    {
        m_ReadFromDataSupplier = DateUtil.getTimestamp();
    }

    String getReadTimeStamp()
    {
        return m_ReadFromDataSupplier;
    }
}