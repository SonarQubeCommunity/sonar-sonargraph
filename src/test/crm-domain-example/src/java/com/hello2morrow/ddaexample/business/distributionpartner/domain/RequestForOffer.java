package com.hello2morrow.ddaexample.business.distributionpartner.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForOfferDsi;
import com.hello2morrow.ddaexample.business.request.domain.Request;

/**
 * @dda-generate-cmp
 */
public final class RequestForOffer extends Request
{
    /**
     * required for creation direct from data source 
     */
    public RequestForOffer(RequestForOfferDsi dsi, DataSupplierReadMarker marker)
    {
        super(dsi, marker);
    }

    /**
     * creates a persistent request for offer 
     */
    public RequestForOffer()
    {
        super((RequestForOfferDsi) getDataManager(RequestForOfferDsi.class).createDataSupplier(
                        RequestForOfferDsi.class, true));
    }
}