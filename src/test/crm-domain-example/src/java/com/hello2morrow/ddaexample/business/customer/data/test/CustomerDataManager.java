/**
 * 23.05.2005 17:28:22 - generated 'data-manager-test (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.customer.data.test;

import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi;

public class CustomerDataManager 
	extends com.hello2morrow.ddaexample.business.contact.data.test.PersonDataManager
	implements com.hello2morrow.ddaexample.business.customer.dsi.CustomerDmi
{
    public DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
    {
        assert dataSupplierInterfaceClass != null;
        assert CustomerDsi.class.equals(dataSupplierInterfaceClass) 
        	: "wrong class = " + dataSupplierInterfaceClass.getName();

        DataSupplierIf created = null;

		if (isPersistent)
		{
			created = new com.hello2morrow.ddaexample.business.customer.data.test.CustomerDataSupplier();
			persistentDataSupplierCreated(created);
		}
		else
		{
			created = new com.hello2morrow.ddaexample.business.customer.data.trans.CustomerDataSupplier();
		}

        return created;
    }

	public com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] findAllCustomers()
	{
        com.hello2morrow.ddaexample.business.contact.data.test.PersonDataSupplier[] all = getAll();
        java.util.List specific = new java.util.ArrayList(all.length);
        for (int i = 0; i < all.length; i++)
        {
            com.hello2morrow.ddaexample.business.contact.data.test.PersonDataSupplier next = all[i];
            if (next instanceof com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi)
            {
                specific.add(next);
            }
        }
        
		return (com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[])specific.toArray(new com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[0]);
	}
	
	public com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] findCustomerByFirstNameAndLastName(java.lang.String firstName, java.lang.String lastName)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException
	{
		com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi[] all = getAll();
        java.util.List found = new java.util.ArrayList();

        for (int i = 0; i < all.length; i++)
        {
			com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi next = all[i];
			if (next instanceof com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi)
			{
	            com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi casted = (com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi)next;
				if
				(
					casted.getFirstName().equals(firstName)
					&& casted.getLastName().equals(lastName)
				)
				{
					found.add(casted);
				}
			}
		}

		return (com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[])found.toArray(new com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[0]);
	}
}