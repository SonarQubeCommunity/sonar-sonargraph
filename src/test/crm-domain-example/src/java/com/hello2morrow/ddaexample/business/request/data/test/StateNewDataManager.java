/**
 * 23.05.2005 17:28:23 - generated 'data-manager-test (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.request.domain.State'
 */

package com.hello2morrow.ddaexample.business.request.data.test;

import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.ddaexample.business.request.dsi.StateNewDsi;

public class StateNewDataManager 
	extends com.hello2morrow.ddaexample.business.request.data.test.StateDataManager
	implements com.hello2morrow.ddaexample.business.request.dsi.StateNewDmi
{
    public DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
    {
        assert dataSupplierInterfaceClass != null;
        assert StateNewDsi.class.equals(dataSupplierInterfaceClass) 
        	: "wrong class = " + dataSupplierInterfaceClass.getName();

        DataSupplierIf created = null;

		if (isPersistent)
		{
			created = new com.hello2morrow.ddaexample.business.request.data.test.StateNewDataSupplier();
			persistentDataSupplierCreated(created);
		}
		else
		{
			created = new com.hello2morrow.ddaexample.business.request.data.trans.StateNewDataSupplier();
		}

        return created;
    }

}