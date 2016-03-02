package com.hello2morrow.ddaexample.business.distributionpartner.domain;

import java.util.Date;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForTestDriveDsi;
import com.hello2morrow.ddaexample.business.request.domain.Request;
import com.hello2morrow.ddaexample.business.request.dsi.RequestDsi;

/**
 * @dda-generate-cmp
 */
public final class RequestForTestDrive extends Request
{
    public RequestForTestDrive(RequestForTestDriveDsi dataSupplier, DataSupplierReadMarker marker)
    {
        super(dataSupplier, marker);
    }

    public RequestForTestDrive()
    {
        super((RequestDsi) getDataManager(RequestForTestDriveDsi.class).createDataSupplier(
                        RequestForTestDriveDsi.class, true));
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public Date getTargetDate()
    {
        return ((RequestForTestDriveDsi) getDataSupplier()).getTargetDate();
    }

    public void setTargetDate(Date targetDate)
    {
        assert targetDate != null;
        ((RequestForTestDriveDsi) getDataSupplier()).setTargetDate(targetDate);
    }
}