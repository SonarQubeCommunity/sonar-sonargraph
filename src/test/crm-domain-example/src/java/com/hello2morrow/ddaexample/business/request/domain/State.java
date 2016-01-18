package com.hello2morrow.ddaexample.business.request.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
import com.hello2morrow.ddaexample.business.request.dsi.StateDsi;

/**
 * @dda-generate-cmp
 */
public abstract class State extends DomainObjectWithDataSupplier
{
    /**
     * required for creation direct from data source 
     */
    public State(StateDsi dsi, DataSupplierReadMarker marker)
    {
        super(dsi, marker);
    }

    /**
     * create a persistent state - required for the creation of subclasses
     */
    protected State(StateDsi dsi) throws TechnicalException
    {
        super(dsi);
    }
}