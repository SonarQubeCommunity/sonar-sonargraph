/**
 * 23.05.2005 17:28:16 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForInformation'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.test;

import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForInformationDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class RequestForInformationDataSupplier extends com.hello2morrow.ddaexample.business.request.data.test.RequestDataSupplier
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

}