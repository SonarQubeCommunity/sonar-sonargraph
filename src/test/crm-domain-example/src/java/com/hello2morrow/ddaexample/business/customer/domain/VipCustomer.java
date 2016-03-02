package com.hello2morrow.ddaexample.business.customer.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
import com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDmi;
import com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi;

/**
 * @dda-generate-cmp
 */
public class VipCustomer extends Customer
{
    /**
     * @dda-dmi-find
     */
    public static VipCustomer[] findVipCustomerByFirstNameAndLastName(String firstName, String lastName)
                    throws TechnicalException
    {
        assert firstName != null;
        assert firstName.length() > 0;
        assert lastName != null;
        assert lastName.length() > 0;
        VipCustomerDmi dmi = (VipCustomerDmi) getDataManager(VipCustomerDsi.class);
        VipCustomerDsi[] all = dmi.findVipCustomerByFirstNameAndLastName(firstName, lastName);
        return (VipCustomer[]) getDomainObjects(all, VipCustomer.class);
    }

    /**
     * required for creation direct from data source 
     */
    public VipCustomer(VipCustomerDsi dataSupplier, DataSupplierReadMarker marker)
    {
        super(dataSupplier, marker);
    }

    /**
     * create a new persistent customer
     */
    public VipCustomer() throws TechnicalException
    {
        super((VipCustomerDsi) getDataManager(VipCustomerDsi.class).createDataSupplier(VipCustomerDsi.class, true));
    }
}