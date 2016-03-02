/**
 * 23.05.2005 17:28:19 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.request.domain.State'
 */

package com.hello2morrow.ddaexample.business.request.data.trans;

import com.hello2morrow.ddaexample.business.request.dsi.StateDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class StateDataSupplier extends com.hello2morrow.dda.business.common.dsi.TransientDataSupplier implements StateDsi
{
	protected StateDataSupplier(DomainObjectId id)
	{
		super(id);
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(StateDsi.class);
        assert dmi != null;
        StateDsi created = (StateDsi) dmi.createDataSupplier(StateDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		StateDsi concrete = (StateDsi)persistent;
		
	}
}