/**
 * 23.05.2005 17:28:22 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.test;

import com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class SalesAssistantDataSupplier extends com.hello2morrow.ddaexample.business.contact.data.test.PersonDataSupplier
	implements SalesAssistantDsi
{
	protected SalesAssistantDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public SalesAssistantDataSupplier()
	{
		super(new DomainObjectId(SalesAssistantDsi.class));
	}

	private java.util.Set m_Customers = new java.util.HashSet();

	public com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] getCustomers()
	{
		return (com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[])m_Customers.toArray(new com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[0]);
	}

	public void setCustomers(com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] set)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
        assert com.hello2morrow.dda.foundation.common.exception.AssertionUtility.checkArray(set);
        m_Customers.clear();
        for (int i = 0; i < set.length; i++)
        {
            addToCustomers(set[i]);
        }
	}

	public void addToCustomers(com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi add)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
		assert add != null;
		if ( !m_Customers.add(add) )
		{
			throw new com.hello2morrow.dda.foundation.common.exception.BusinessException("unable to add object = " + add);
		}
	}

	public void removeFromCustomers(com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi remove)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
		assert remove != null;
		if ( !m_Customers.remove(remove) )
		{
			throw new com.hello2morrow.dda.foundation.common.exception.BusinessException("unable to remove object = " + remove);
		}
	}

}