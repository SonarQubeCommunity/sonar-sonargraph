/**
 * 23.05.2005 17:28:20 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.customer.domain.VipCustomer'
 */

package com.hello2morrow.ddaexample.business.customer.data.trans;

import com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class VipCustomerDataSupplier extends com.hello2morrow.ddaexample.business.customer.data.trans.CustomerDataSupplier
	 implements VipCustomerDsi
{
	protected VipCustomerDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public VipCustomerDataSupplier()
	{
		super(new DomainObjectId(VipCustomerDsi.class));
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(VipCustomerDsi.class);
        assert dmi != null;
        VipCustomerDsi created = (VipCustomerDsi) dmi.createDataSupplier(VipCustomerDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		VipCustomerDsi concrete = (VipCustomerDsi)persistent;
		
	}
}