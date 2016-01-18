/**
 * 23.05.2005 17:28:22 - generated 'data-manager-test (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.test;

import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi;

public class SalesAssistantDataManager 
	extends com.hello2morrow.ddaexample.business.contact.data.test.PersonDataManager
	implements com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDmi
{
    public DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
    {
        assert dataSupplierInterfaceClass != null;
        assert SalesAssistantDsi.class.equals(dataSupplierInterfaceClass) 
        	: "wrong class = " + dataSupplierInterfaceClass.getName();

        DataSupplierIf created = null;

		if (isPersistent)
		{
			created = new com.hello2morrow.ddaexample.business.distributionpartner.data.test.SalesAssistantDataSupplier();
			persistentDataSupplierCreated(created);
		}
		else
		{
			created = new com.hello2morrow.ddaexample.business.distributionpartner.data.trans.SalesAssistantDataSupplier();
		}

        return created;
    }

	public com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi[] findSalesAssistantByFirstNameAndLastName(java.lang.String firstName, java.lang.String lastName)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException
	{
		com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi[] all = getAll();
        java.util.List found = new java.util.ArrayList();

        for (int i = 0; i < all.length; i++)
        {
			com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi next = all[i];
			if (next instanceof com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi)
			{
	            com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi casted = (com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi)next;
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

		return (com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi[])found.toArray(new com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi[0]);
	}
}