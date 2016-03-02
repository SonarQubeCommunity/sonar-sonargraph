/**
 * 23.05.2005 17:28:23 - generated 'data-manager-test (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.request.domain.Request'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.data.test;

import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForInformationDsi;

public class RequestForInformationDataManager 
	extends com.hello2morrow.ddaexample.business.request.data.test.RequestDataManager
	implements com.hello2morrow.ddaexample.business.distributionpartner.dsi.RequestForInformationDmi
{
    public DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
    {
        assert dataSupplierInterfaceClass != null;
        assert RequestForInformationDsi.class.equals(dataSupplierInterfaceClass) 
        	: "wrong class = " + dataSupplierInterfaceClass.getName();

        DataSupplierIf created = null;

		if (isPersistent)
		{
			created = new com.hello2morrow.ddaexample.business.distributionpartner.data.test.RequestForInformationDataSupplier();
			persistentDataSupplierCreated(created);
		}
		else
		{
			created = new com.hello2morrow.ddaexample.business.distributionpartner.data.trans.RequestForInformationDataSupplier();
		}

        return created;
    }

}