package com.hello2morrow.ddaexample.business.distributionpartner.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.ddaexample.business.contact.domain.Person;
import com.hello2morrow.ddaexample.business.customer.domain.Customer;
import com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi;
import com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDmi;
import com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi;

/**
 * @dda-generate-cmp
 */
public final class SalesAssistant extends Person
{
    /**
     * @dda-dmi-find
     */
    public static SalesAssistant[] findSalesAssistantByFirstNameAndLastName(String firstName, String lastName)
    {
        assert firstName != null;
        assert firstName.length() > 0;
        assert lastName != null;
        assert lastName.length() > 0;
        SalesAssistantDmi dmi = (SalesAssistantDmi) getDataManager(SalesAssistantDsi.class);
        SalesAssistantDsi[] all = dmi.findSalesAssistantByFirstNameAndLastName(firstName, lastName);
        return (SalesAssistant[]) getDomainObjects(all, SalesAssistant.class);
    }

    /**
     * required for creation direct from data source 
     */
    public SalesAssistant(SalesAssistantDsi dataSupplier, DataSupplierReadMarker marker)
    {
        super(dataSupplier, marker);
    }

    public SalesAssistant()
    {
        super((SalesAssistantDsi) getDataManager(SalesAssistantDsi.class).createDataSupplier(SalesAssistantDsi.class,
                        true));
    }

    /**
     * @dda-dto
     * @dda-dsi 1-n no-duplicates
     */
    public Customer[] getCustomers()
    {
        CustomerDsi[] all = ((SalesAssistantDsi) getDataSupplier()).getCustomers();
        return (Customer[]) getDomainObjects(all, Customer.class);
    }

    public void addToCustomers(Customer customer)
    {
        assert customer != null;
        ((SalesAssistantDsi) getDataSupplier()).addToCustomers((CustomerDsi) customer.getDataSupplier());
    }

    public void setCustomers(Customer[] customers)
    {
        assert AssertionUtility.checkArray(customers);
        CustomerDsi[] all = (CustomerDsi[]) getDataSuppliers(customers, CustomerDsi.class);
        ((SalesAssistantDsi) getDataSupplier()).setCustomers(all);
    }
}