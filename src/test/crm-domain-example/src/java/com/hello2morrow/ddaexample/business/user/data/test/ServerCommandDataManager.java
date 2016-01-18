/**
 * 23.05.2005 17:28:16 - generated 'data-manager-test' for 'com.hello2morrow.ddaexample.business.user.domain.ServerCommand'
 */

package com.hello2morrow.ddaexample.business.user.data.test;

import java.util.HashMap;
import java.util.Map;

import com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi;
import com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDmi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public final class ServerCommandDataManager implements ServerCommandDmi
{
	private Map m_ObjectIdToDsiImpl = new HashMap();
	
	protected final ServerCommandDataSupplier[] getAll()
	{
		return (ServerCommandDataSupplier[])m_ObjectIdToDsiImpl.values().toArray(new ServerCommandDataSupplier[0]);
	}
		
    public final boolean supportsPersistentData()
    {
    	return true;
    }

	public final DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean persistent)
	{
		assert dataSupplierInterfaceClass != null;
		assert dataSupplierInterfaceClass.equals(ServerCommandDsi.class);
		
		if(persistent)
		{
            ServerCommandDataSupplier created = new ServerCommandDataSupplier();
            assert !m_ObjectIdToDsiImpl.containsKey(created.getObjectId());
            m_ObjectIdToDsiImpl.put(created.getObjectId(), created);
        	return created;
        }
        else
        {
        	return new com.hello2morrow.ddaexample.business.user.data.trans.ServerCommandDataSupplier();
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

	public com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[] findAllServerCommands()
	{
		return getAll();
	}
	
	public com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi findServerCommandByName(java.lang.String name) 
		throws TechnicalException
	{
		ServerCommandDsi[] all = getAll();
        for (int i = 0; i < all.length; i++)
        {
            ServerCommandDsi next = all[i];
			if
			(
				next.getName().equals(name)
			)
			{
				return next;
			}
		}

		return null;
	}
}