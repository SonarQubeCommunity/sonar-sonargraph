package com.hello2morrow.ddaexample.business.user.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.foundation.common.DateUtil;
import com.hello2morrow.ddaexample.business.user.dsi.LoginEventDmi;
import com.hello2morrow.ddaexample.business.user.dsi.LoginEventDsi;

/**
 * @dda-generate-cmp
 */
public final class LoginEvent extends DomainObjectWithDataSupplier
{
    /**
     * @dda-dmi-find
     */
    public static LoginEvent[] findAllLoginEvents()
    {
        LoginEventDmi dmi = (LoginEventDmi) getDataManager(LoginEventDsi.class);
        LoginEventDsi[] all = dmi.findAllLoginEvents();
        return (LoginEvent[]) getDomainObjects(all, LoginEvent.class);
    }

    public LoginEvent(LoginEventDsi dataSupplier, DataSupplierReadMarker marker)
    {
        super(dataSupplier, marker);
    }

    LoginEvent(String userName)
    {
        super(getDataManager(LoginEventDsi.class).createDataSupplier(LoginEventDsi.class, true));
        setUserName(userName);
        setCreatedTimestamp(DateUtil.getTimestamp());
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getUserName()
    {
        return ((LoginEventDsi) getDataSupplier()).getUserName();
    }

    void setUserName(String userName)
    {
        assert userName != null;
        assert userName.length() > 0;
        ((LoginEventDsi) getDataSupplier()).setUserName(userName);
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getCreatedTimestamp()
    {
        return ((LoginEventDsi) getDataSupplier()).getCreatedTimestamp();
    }

    private void setCreatedTimestamp(String createdTimestamp)
    {
        assert createdTimestamp != null;
        ((LoginEventDsi) getDataSupplier()).setCreatedTimestamp(createdTimestamp);
    }
}