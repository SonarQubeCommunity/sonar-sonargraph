/**
 * 23.05.2005 17:28:20 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.trans;

import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForOfferDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class RequestForOfferDataSupplier extends com.hello2morrow.ddaexample.business.request.data.trans.RequestDataSupplier
	 implements RequestForOfferDsi
{
	protected RequestForOfferDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public RequestForOfferDataSupplier()
	{
		super(new DomainObjectId(RequestForOfferDsi.class));
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(RequestForOfferDsi.class);
        assert dmi != null;
        RequestForOfferDsi created = (RequestForOfferDsi) dmi.createDataSupplier(RequestForOfferDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		RequestForOfferDsi concrete = (RequestForOfferDsi)persistent;
		
	}
}