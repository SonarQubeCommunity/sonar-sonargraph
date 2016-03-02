/**
 * 23.05.2005 17:28:23 - generated 'data-manager-test (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.request.domain.Request'
 */

package com.hello2morrow.ddaexample.business.request.data.test;

import com.hello2morrow.ddaexample.business.request.dsi.RequestDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.business.common.dsi.TestDataSupplierInheritanceCache;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class RequestDataManager 
	implements com.hello2morrow.ddaexample.business.request.dsi.RequestDmi
{
	protected final void persistentDataSupplierCreated(DataSupplierIf created)
	{
		assert created != null;
		assert created instanceof RequestDataSupplier;
		assert created.supportsPersistentData();
		TestDataSupplierInheritanceCache.getInstance().add(created, RequestDataManager.class);
	}
	
    protected final RequestDataSupplier[] getAll()
    {
        return (RequestDataSupplier[]) TestDataSupplierInheritanceCache.getInstance().getAll(RequestDataManager.class, new RequestDataSupplier[0]);
    }

    public final boolean supportsPersistentData()
    {
    	return true;
    }

    public DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
    {
        assert dataSupplierInterfaceClass != null;
        assert RequestDsi.class.equals(dataSupplierInterfaceClass) 
        	: "wrong class = " + dataSupplierInterfaceClass.getName();
		assert false : "com.hello2morrow.ddaexample.business.request.domain.Request is abstract" ;
		return null;
    }

    public final void deleteDataSupplier(DataSupplierIf dataSupplier) throws TechnicalException
    {
        assert dataSupplier != null;
        assert TestDataSupplierInheritanceCache.getInstance().contains(dataSupplier.getObjectId());
        TestDataSupplierInheritanceCache.getInstance().delete(dataSupplier, RequestDataManager.class);
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

	public com.hello2morrow.ddaexample.business.request.dsi.RequestDsi[] findAllRequests()
	{
		return getAll();
	}
	
}



