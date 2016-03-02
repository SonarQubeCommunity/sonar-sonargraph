package com.hello2morrow.ddaexample.business.distributionpartner.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForInformationDsi;
import com.hello2morrow.ddaexample.business.request.domain.Request;

/**
 * @dda-generate-cmp
 */
public class RequestForInformation extends Request
{
    /**
     * required for creation direct from data source 
     */
    public RequestForInformation(RequestForInformationDsi dsi, DataSupplierReadMarker marker)
    {
        super(dsi, marker);
    }

    /**
     * creates a persistent request for information 
     */
    public RequestForInformation()
    {
        super((RequestForInformationDsi) getDataManager(RequestForInformationDsi.class).createDataSupplier(
                        RequestForInformationDsi.class, true));
    }
}