/**
 * 23.05.2005 17:28:21 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.trans;

import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForTestDriveDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class RequestForTestDriveDataSupplier extends com.hello2morrow.ddaexample.business.request.data.trans.RequestDataSupplier
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

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(RequestForTestDriveDsi.class);
        assert dmi != null;
        RequestForTestDriveDsi created = (RequestForTestDriveDsi) dmi.createDataSupplier(RequestForTestDriveDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		RequestForTestDriveDsi concrete = (RequestForTestDriveDsi)persistent;
		
		concrete.setTargetDate(getTargetDate());
	}
}