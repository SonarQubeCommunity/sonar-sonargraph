/**
 * 23.05.2005 17:28:16 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.request.domain.StateNew'
 */

package com.hello2morrow.ddaexample.business.request.data.test;

import com.hello2morrow.ddaexample.business.request.dsi.StateNewDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class StateNewDataSupplier extends com.hello2morrow.ddaexample.business.request.data.test.StateDataSupplier
	implements StateNewDsi
{
	protected StateNewDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public StateNewDataSupplier()
	{
		super(new DomainObjectId(StateNewDsi.class));
	}

}