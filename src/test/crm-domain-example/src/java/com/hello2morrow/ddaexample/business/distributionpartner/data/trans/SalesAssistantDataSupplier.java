/**
 * 23.05.2005 17:28:22 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.trans;

import com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class SalesAssistantDataSupplier extends com.hello2morrow.ddaexample.business.contact.data.trans.PersonDataSupplier
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
	{
        assert com.hello2morrow.dda.foundation.common.exception.AssertionUtility.checkArray(set);
        m_Customers.clear();
        m_Customers.addAll(java.util.Arrays.asList(set));
	}

	public void addToCustomers(com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi add) throws BusinessException
	{
		assert add != null;
		if ( !m_Customers.add(add) )
		{
			throw new BusinessException("unable to add object = " + add);
		}
	}

	public void removeFromCustomers(com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi remove) throws BusinessException
	{
		assert remove != null;
		if ( !m_Customers.remove(remove) )
		{
			throw new BusinessException("unable to remove object = " + remove);
		}
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(SalesAssistantDsi.class);
        assert dmi != null;
        SalesAssistantDsi created = (SalesAssistantDsi) dmi.createDataSupplier(SalesAssistantDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		SalesAssistantDsi concrete = (SalesAssistantDsi)persistent;
		
		com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] customers = getCustomers();
		com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] mapped = new com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[customers.length];
		
		for (int i = 0; i < customers.length; ++i)
		{
			com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi nextFromCustomers = customers[i];
			if(!nextFromCustomers.supportsPersistentData())
			{
				mapped[i] = (com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi)((com.hello2morrow.dda.business.common.dsi.TransientDataSupplier)nextFromCustomers).save();
			}
			else
			{
				mapped[i] = nextFromCustomers; 
			}
		} 		

		concrete.setCustomers(mapped);
	
	}
}