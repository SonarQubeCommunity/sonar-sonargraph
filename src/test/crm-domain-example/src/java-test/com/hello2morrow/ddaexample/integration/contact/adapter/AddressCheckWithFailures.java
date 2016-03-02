package com.hello2morrow.ddaexample.integration.contact.adapter;

import com.hello2morrow.ddaexample.integration.contact.esi.AddressCheckEsi;

public final class AddressCheckWithFailures implements AddressCheckEsi
{
    public boolean checkAddress(String street, String city, String zipCode)
    {
        return false;
    }
}
