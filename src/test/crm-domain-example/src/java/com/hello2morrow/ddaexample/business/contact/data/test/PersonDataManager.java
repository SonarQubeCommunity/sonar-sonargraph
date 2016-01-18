/**
 * 23.05.2005 17:28:22 - generated 'data-manager-test (class hierarchy)' for root class 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.contact.data.test;

import com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.business.common.dsi.TestDataSupplierInheritanceCache;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class PersonDataManager 
	implements com.hello2morrow.ddaexample.business.contact.dsi.PersonDmi
{
	protected final void persistentDataSupplierCreated(DataSupplierIf created)
	{
		assert created != null;
		assert created instanceof PersonDataSupplier;
		assert created.supportsPersistentData();
		TestDataSupplierInheritanceCache.getInstance().add(created, PersonDataManager.class);
	}
	
    protected final PersonDataSupplier[] getAll()
    {
        return (PersonDataSupplier[]) TestDataSupplierInheritanceCache.getInstance().getAll(PersonDataManager.class, new PersonDataSupplier[0]);
    }

    public final boolean supportsPersistentData()
    {
    	return true;
    }

    public DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
    {
        assert dataSupplierInterfaceClass != null;
        assert PersonDsi.class.equals(dataSupplierInterfaceClass) 
        	: "wrong class = " + dataSupplierInterfaceClass.getName();
		assert false : "com.hello2morrow.ddaexample.business.contact.domain.Person is abstract" ;
		return null;
    }

    public final void deleteDataSupplier(DataSupplierIf dataSupplier) throws TechnicalException
    {
        assert dataSupplier != null;
        assert TestDataSupplierInheritanceCache.getInstance().contains(dataSupplier.getObjectId());
        TestDataSupplierInheritanceCache.getInstance().delete(dataSupplier, PersonDataManager.class);
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



