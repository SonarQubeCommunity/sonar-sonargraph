package com.hello2morrow.ddaexample.integration.contact.esi;

import com.hello2morrow.dda.integration.common.esi.ExternalSystemIf;

public interface AddressCheckEsi extends ExternalSystemIf
{
    public boolean checkAddress(String street, String city, String zipCode);
}
