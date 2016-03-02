package com.hello2morrow.ddaexample.business.contact.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi;
import com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi;

/**
 * @dda-generate-cmp
 */
public abstract class Person extends DomainObjectWithDataSupplier
{
    /**
     * required for creation direct from data source 
     */
    public Person(PersonDsi dataSupplier, DataSupplierReadMarker marker)
    {
        super(dataSupplier, marker);
//        new SQLException();
    }

    /**
     * required for subclasses
     */
    public Person(PersonDsi dataSupplier)
    {
        super(dataSupplier);
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getFirstName()
    {
        return ((PersonDsi) getDataSupplier()).getFirstName();
    }

    public void setFirstName(String firstName)
    {
        assert firstName != null;
        assert firstName.length() > 0;
        ((PersonDsi) getDataSupplier()).setFirstName(firstName);
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getLastName()
    {
        return ((PersonDsi) getDataSupplier()).getLastName();
    }

    public void setLastName(String lastName)
    {
        assert lastName != null;
        assert lastName.length() > 0;
        ((PersonDsi) getDataSupplier()).setLastName(lastName);
    }

    /**
     * @dda-dto
     * @dda-dsi 1-1 target-cascade-delete
     */
    public Address getAddress()
    {
        return (Address) getDomainObject(((PersonDsi) getDataSupplier()).getAddress());
    }

    public void setAddress(Address address)
    {
        assert address != null;
        ((PersonDsi) getDataSupplier()).setAddress((AddressDsi) address.getDataSupplier());
    }
}