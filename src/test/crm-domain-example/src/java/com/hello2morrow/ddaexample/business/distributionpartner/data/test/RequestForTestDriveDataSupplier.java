/**
 * 23.05.2005 17:28:21 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.test;

import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForTestDriveDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class RequestForTestDriveDataSupplier extends com.hello2morrow.ddaexample.business.request.data.test.RequestDataSupplier
	implements RequestForTestDriveDsi
{
	protected RequestForTestDriveDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public RequestForTestDriveDataSupplier()
	{
		super(new DomainObjectId(RequestForTestDriveDsi.class));
	}

	private java.util.Date m_TargetDate = null; 

	public java.util.Date getTargetDate()
	{
		return m_TargetDate;
	}

	public void setTargetDate(java.util.Date set)
	{
		m_TargetDate = set;
	}

}