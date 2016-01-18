package com.hello2morrow.ddaexample.business.contact.domain;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.integration.common.esi.EsiFactory;
import com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi;
import com.hello2morrow.ddaexample.integration.contact.esi.AddressCheckEsi;

/**
 * @dda-generate-cmp
 */
public final class Address extends DomainObjectWithDataSupplier
{
    /**
     * required for creation direct from data source 
     */
    public Address(AddressDsi dataSupplier, DataSupplierReadMarker marker)
    {
        super(dataSupplier, marker);
        
    }

//    public void setPerson(Person p)
//    {
//        
//    }
    /**
     * create a new transient or persistent address 
     */
    public Address(int storageType)
    {
        super(getDataManager(AddressDsi.class).createDataSupplier(AddressDsi.class, makePersistent(storageType)));
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getStreet()
    {
        return ((AddressDsi) getDataSupplier()).getStreet();
    }

    public void setStreet(String street)
    {
        assert street != null;
        assert street.length() > 0;
        ((AddressDsi) getDataSupplier()).setStreet(street);
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getCity()
    {
        return ((AddressDsi) getDataSupplier()).getCity();
    }

    public void setCity(String city)
    {
        assert city != null;
        assert city.length() > 0;
        ((AddressDsi) getDataSupplier()).setCity(city);
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getZipCode()
    {
        return ((AddressDsi) getDataSupplier()).getZipCode();
    }

    public void setZipCode(String zipCode)
    {
        assert zipCode != null;
        assert zipCode.length() > 0;
        ((AddressDsi) getDataSupplier()).setZipCode(zipCode);
    }

    public boolean isValid()
    {
        AddressCheckEsi esi = (AddressCheckEsi) EsiFactory.getInstance().getEsiImplementation(AddressCheckEsi.class);
        return esi.checkAddress(getStreet(), getCity(), getZipCode());
    }

    public void save()
    {
        assert isValid();
        super.save();
    }

    public boolean isSameAddress(Address address)
    {
        assert address != null;
        return getStreet().equals(address.getStreet()) && getCity().equals(address.getCity())
                        && getZipCode().equals(address.getZipCode());
    }
}