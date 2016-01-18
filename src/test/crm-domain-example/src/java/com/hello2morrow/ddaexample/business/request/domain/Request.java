package com.hello2morrow.ddaexample.business.request.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.foundation.common.DateUtil;
import com.hello2morrow.ddaexample.business.request.dsi.RequestDmi;
import com.hello2morrow.ddaexample.business.request.dsi.RequestDsi;
import com.hello2morrow.ddaexample.business.request.dsi.StateDsi;

/**
 * @dda-generate-cmp
 */
public abstract class Request extends DomainObjectWithDataSupplier
{
    /**
     * @dda-dmi-find
     */
    public static Request[] findAllRequests()
    {
        RequestDmi dmi = (RequestDmi) getDataManager(RequestDsi.class);
        return (Request[]) getDomainObjects(dmi.findAllRequests(), Request.class);
    }

    /**
     * required for creation direct from data source 
     */
    public Request(RequestDsi dsi, DataSupplierReadMarker marker)
    {
        super(dsi, marker);
    }

    /**
     * creates a persistent request - required for the creation of subclasses 
     */
    protected Request(RequestDsi dsi)
    {
        super(dsi);
        setState(new StateNew());
        setCreatedTimestamp(DateUtil.getTimestamp());
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getSubject()
    {
        return ((RequestDsi) getDataSupplier()).getSubject();
    }

    public void setSubject(String subject)
    {
        assert subject != null;
        assert subject.length() > 0;
        ((RequestDsi) getDataSupplier()).setSubject(subject);
    }

    /**
     * @dda-dsi
     */
    public String getCreatedTimestamp()
    {
        return ((RequestDsi) getDataSupplier()).getCreatedTimestamp();
    }

    private void setCreatedTimestamp(String createdTimestamp)
    {
        assert createdTimestamp != null;
        ((RequestDsi) getDataSupplier()).setCreatedTimestamp(createdTimestamp);
    }

    /**
     * @dda-dsi 1-1 target-cascade-delete
     */
    public State getState()
    {
        return (State) getDomainObject(((RequestDsi) getDataSupplier()).getState());
    }

    protected void setState(State state)
    {
        assert state != null;
        ((RequestDsi) getDataSupplier()).setState((StateDsi) state.getDataSupplier());
    }
}