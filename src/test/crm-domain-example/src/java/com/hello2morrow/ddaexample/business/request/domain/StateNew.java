package com.hello2morrow.ddaexample.business.request.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.ddaexample.business.request.dsi.StateNewDsi;

/**
 * @dda-generate-cmp
 */
public final class StateNew extends State
{
    /**
     * required for creation direct from data source 
     */
    public StateNew(StateNewDsi dsi, DataSupplierReadMarker marker)
    {
        super(dsi, marker);
    }

    /**
     * create a new persistent state new 
     */
    public StateNew()
    {
        super((StateNewDsi) getDataManager(StateNewDsi.class).createDataSupplier(StateNewDsi.class, true));
    }
}