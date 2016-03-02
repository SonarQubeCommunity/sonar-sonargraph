/**
 * 23.05.2005 17:28:16 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForInformation'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.trans;

import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForInformationDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class RequestForInformationDataSupplier extends com.hello2morrow.ddaexample.business.request.data.trans.RequestDataSupplier
	 implements RequestForInformationDsi
{
	protected RequestForInformationDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public RequestForInformationDataSupplier()
	{
		super(new DomainObjectId(RequestForInformationDsi.class));
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(RequestForInformationDsi.class);
        assert dmi != null;
        RequestForInformationDsi created = (RequestForInformationDsi) dmi.createDataSupplier(RequestForInformationDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		RequestForInformationDsi concrete = (RequestForInformationDsi)persistent;
		
	}
}