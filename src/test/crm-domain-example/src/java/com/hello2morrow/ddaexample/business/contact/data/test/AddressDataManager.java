/**
 * 23.05.2005 17:28:21 - generated 'data-manager-test' for 'com.hello2morrow.ddaexample.business.contact.domain.Address'
 */

package com.hello2morrow.ddaexample.business.contact.data.test;

import java.util.HashMap;
import java.util.Map;

import com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi;
import com.hello2morrow.ddaexample.business.contact.dsi.AddressDmi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public final class AddressDataManager implements AddressDmi
{
	private Map m_ObjectIdToDsiImpl = new HashMap();
	
	protected final AddressDataSupplier[] getAll()
	{
		return (AddressDataSupplier[])m_ObjectIdToDsiImpl.values().toArray(new AddressDataSupplier[0]);
	}
		
    public final boolean supportsPersistentData()
    {
    	return true;
    }

	public final DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean persistent)
	{
		assert dataSupplierInterfaceClass != null;
		assert dataSupplierInterfaceClass.equals(AddressDsi.class);
		
		if(persistent)
		{
            AddressDataSupplier created = new AddressDataSupplier();
            assert !m_ObjectIdToDsiImpl.containsKey(created.getObjectId());
            m_ObjectIdToDsiImpl.put(created.getObjectId(), created);
        	return created;
        }
        else
        {
        	return new com.hello2morrow.ddaexample.business.contact.data.trans.AddressDataSupplier();
        }
	}

    public final void deleteDataSupplier(DataSupplierIf dataSupplier) throws TechnicalException
    {
        assert dataSupplier != null;
        assert m_ObjectIdToDsiImpl.containsKey(dataSupplier.getObjectId());
        m_ObjectIdToDsiImpl.remove(dataSupplier.getObjectId());
        DomainObjectIf domainObject = dataSupplier.getDomainObject();
        assert domainObject != null;
        domainObject.markDeleted((DomainObjectId) dataSupplier.getObjectId());
    }

    public DataSupplierIf findByObjectId(ObjectIdIf id) throws TechnicalException
    {
    	assert id != null;
    	assert id instanceof DomainObjectId;
        assert m_ObjectIdToDsiImpl.containsKey(id);
		return (DataSupplierIf)m_ObjectIdToDsiImpl.get(id);
    }

}