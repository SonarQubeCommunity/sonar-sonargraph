/**
 * 23.05.2005 17:28:20 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.customer.domain.VipCustomer'
 */

package com.hello2morrow.ddaexample.business.customer.data.test;

import com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class VipCustomerDataSupplier extends com.hello2morrow.ddaexample.business.customer.data.test.CustomerDataSupplier
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

}