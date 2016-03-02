/**
 * 23.05.2005 17:28:23 - generated 'data-manager-test (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.request.domain.State'
 */

package com.hello2morrow.ddaexample.business.request.data.test;

import com.hello2morrow.ddaexample.business.request.dsi.StateDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.business.common.dsi.TestDataSupplierInheritanceCache;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class StateDataManager 
	implements com.hello2morrow.ddaexample.business.request.dsi.StateDmi
{
	protected final void persistentDataSupplierCreated(DataSupplierIf created)
	{
		assert created != null;
		assert created instanceof StateDataSupplier;
		assert created.supportsPersistentData();
		TestDataSupplierInheritanceCache.getInstance().add(created, StateDataManager.class);
	}
	
    protected final StateDataSupplier[] getAll()
    {
        return (StateDataSupplier[]) TestDataSupplierInheritanceCache.getInstance().getAll(StateDataManager.class, new StateDataSupplier[0]);
    }

    public final boolean supportsPersistentData()
    {
    	return true;
    }

    public DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
    {
        assert dataSupplierInterfaceClass != null;
        assert StateDsi.class.equals(dataSupplierInterfaceClass) 
        	: "wrong class = " + dataSupplierInterfaceClass.getName();
		assert false : "com.hello2morrow.ddaexample.business.request.domain.State is abstract" ;
		return null;
    }

    public final void deleteDataSupplier(DataSupplierIf dataSupplier) throws TechnicalException
    {
        assert dataSupplier != null;
        assert TestDataSupplierInheritanceCache.getInstance().contains(dataSupplier.getObjectId());
        TestDataSupplierInheritanceCache.getInstance().delete(dataSupplier, StateDataManager.class);
        DomainObjectIf domainObject = dataSupplier.getDomainObject();
        assert domainObject != null;
        domainObject.markDeleted((DomainObjectId) dataSupplier.getObjectId());
    }

    public final DataSupplierIf findByObjectId(ObjectIdIf id) throws TechnicalException
    {
    	assert id != null;
    	assert id instanceof DomainObjectId;
        assert TestDataSupplierInheritanceCache.getInstance().contains(id);
    	return TestDataSupplierInheritanceCache.getInstance().findByObjectId(id);
    }

}

