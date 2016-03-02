/**
 * 23.05.2005 17:28:21 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.customer.domain.Customer'
 */

package com.hello2morrow.ddaexample.business.customer.data.trans;

import com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class CustomerDataSupplier extends com.hello2morrow.ddaexample.business.contact.data.trans.PersonDataSupplier
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
	
    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(CustomerDsi.class);
        assert dmi != null;
        CustomerDsi created = (CustomerDsi) dmi.createDataSupplier(CustomerDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		CustomerDsi concrete = (CustomerDsi)persistent;
		
		concrete.setAge(getAge());
	}
}