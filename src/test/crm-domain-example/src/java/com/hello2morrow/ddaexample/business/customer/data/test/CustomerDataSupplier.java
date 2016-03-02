/**
 * 23.05.2005 17:28:21 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.customer.domain.Customer'
 */

package com.hello2morrow.ddaexample.business.customer.data.test;

import com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class CustomerDataSupplier extends com.hello2morrow.ddaexample.business.contact.data.test.PersonDataSupplier
	implements CustomerDsi
{
	protected CustomerDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public CustomerDataSupplier()
	{
		super(new DomainObjectId(CustomerDsi.class));
	}

	private int m_Age; 

	public int getAge()
	{
		return m_Age;
	}

	public void setAge(int set)
	{
		m_Age = set;
	}
	
}