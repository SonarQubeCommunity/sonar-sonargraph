/**
 * 23.05.2005 17:28:20 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.test;

import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForOfferDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class RequestForOfferDataSupplier extends com.hello2morrow.ddaexample.business.request.data.test.RequestDataSupplier
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

}