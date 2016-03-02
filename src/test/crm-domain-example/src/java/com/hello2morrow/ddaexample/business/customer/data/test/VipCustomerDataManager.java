/**
 * 23.05.2005 17:28:22 - generated 'data-manager-test (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.customer.data.test;

import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi;

public class VipCustomerDataManager 
	extends com.hello2morrow.ddaexample.business.customer.data.test.CustomerDataManager
	implements com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDmi
{
     public DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
    {
        assert dataSupplierInterfaceClass != null;
        assert VipCustomerDsi.class.equals(dataSupplierInterfaceClass) 
        	: "wrong class = " + dataSupplierInterfaceClass.getName();

        DataSupplierIf created = null;

		if (isPersistent)
		{
			created = new com.hello2morrow.ddaexample.business.customer.data.test.VipCustomerDataSupplier();
			persistentDataSupplierCreated(created);
		}
		else
		{
			created = new com.hello2morrow.ddaexample.business.customer.data.trans.VipCustomerDataSupplier();
		}

        return created;
    }

	public com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi[] findVipCustomerByFirstNameAndLastName(java.lang.String firstName, java.lang.String lastName)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException
	{
		com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi[] all = getAll();
        java.util.List found = new java.util.ArrayList();

        for (int i = 0; i < all.length; i++)
        {
			com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi next = all[i];
			if (next instanceof com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi)
			{
	            com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi casted = (com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi)next;
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

		return (com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi[])found.toArray(new com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi[0]);
	}
}