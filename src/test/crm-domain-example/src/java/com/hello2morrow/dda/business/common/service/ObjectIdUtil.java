package com.hello2morrow.dda.business.common.service;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;


public final class ObjectIdUtil
{
    private ObjectIdUtil()
    {
        //make it unaccessible
    }

    public static ObjectIdIf[] getIds(DomainObjectDto[] dtos)
    {
        assert AssertionUtility.checkArray(dtos);
        ObjectIdIf[] ids = new ObjectIdIf[dtos.length];
        for (int i = 0; i < dtos.length; i++)
        {
            DomainObjectDto nextDto = dtos[i];
            assert nextDto.hasObjectId();
            ids[i] = nextDto.getObjectId();
        }
        return ids;
    }
}