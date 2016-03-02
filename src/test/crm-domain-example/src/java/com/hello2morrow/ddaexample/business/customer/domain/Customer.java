package com.hello2morrow.ddaexample.business.customer.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.ddaexample.business.contact.domain.Person;
import com.hello2morrow.ddaexample.business.customer.dsi.CustomerDmi;
import com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi;

/**
 * @dda-generate-cmp
 */
public class Customer extends Person
{
    /**
     * @dda-dmi-find
     */
    public static Customer[] findAllCustomers()
    {
        CustomerDmi dmi = (CustomerDmi) getDataManager(CustomerDsi.class);
//        new SQLException();
        return (Customer[]) getDomainObjects(dmi.findAllCustomers(), Customer.class);
    }

    /**
     * @dda-dmi-find
     */
    public static Customer[] findCustomerByFirstNameAndLastName(String firstName, String lastName)
    {
        assert firstName != null;
        assert firstName.length() > 0;
        assert lastName != null;
        assert lastName.length() > 0;
        CustomerDmi dmi = (CustomerDmi) getDataManager(CustomerDsi.class);
        CustomerDsi[] all = dmi.findCustomerByFirstNameAndLastName(firstName, lastName);
        return (Customer[]) getDomainObjects(all, Customer.class);
    }

    /**
     * required for creation direct from data source 
     */
    public Customer(CustomerDsi dataSupplier, DataSupplierReadMarker marker)
    {
        super(dataSupplier, marker);
    }

    /**
     * required for subclasses
     */
    public Customer(CustomerDsi dataSupplier)
    {
        super(dataSupplier);
    }

    /**
     * create a new persistent customer
     */
    public Customer()
    {
        super((CustomerDsi) getDataManager(CustomerDsi.class).createDataSupplier(CustomerDsi.class, true));
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public int getAge()
    {
        return ((CustomerDsi) getDataSupplier()).getAge();
    }

    public void setAge(int age)
    {
        ((CustomerDsi) getDataSupplier()).setAge(age);
    }
}