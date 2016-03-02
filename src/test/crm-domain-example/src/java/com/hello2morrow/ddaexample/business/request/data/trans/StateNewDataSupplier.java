/**
 * 23.05.2005 17:28:16 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.request.domain.StateNew'
 */

package com.hello2morrow.ddaexample.business.request.data.trans;

import com.hello2morrow.ddaexample.business.request.dsi.StateNewDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class StateNewDataSupplier extends com.hello2morrow.ddaexample.business.request.data.trans.StateDataSupplier
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

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(StateNewDsi.class);
        assert dmi != null;
        StateNewDsi created = (StateNewDsi) dmi.createDataSupplier(StateNewDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		StateNewDsi concrete = (StateNewDsi)persistent;
		
	}
}