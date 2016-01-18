/**
 * 23.05.2005 17:28:18 - generated 'data-manager-test' for 'com.hello2morrow.ddaexample.business.user.domain.User'
 */

package com.hello2morrow.ddaexample.business.user.data.test;

import java.util.HashMap;
import java.util.Map;

import com.hello2morrow.ddaexample.business.user.dsi.UserDsi;
import com.hello2morrow.ddaexample.business.user.dsi.UserDmi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public final class UserDataManager implements UserDmi
{
	private Map m_ObjectIdToDsiImpl = new HashMap();
	
	protected final UserDataSupplier[] getAll()
	{
		return (UserDataSupplier[])m_ObjectIdToDsiImpl.values().toArray(new UserDataSupplier[0]);
	}
		
    public final boolean supportsPersistentData()
    {
    	return true;
    }

	public final DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean persistent)
	{
		assert dataSupplierInterfaceClass != null;
		assert dataSupplierInterfaceClass.equals(UserDsi.class);
		
		if(persistent)
		{
            UserDataSupplier created = new UserDataSupplier();
            assert !m_ObjectIdToDsiImpl.containsKey(created.getObjectId());
            m_ObjectIdToDsiImpl.put(created.getObjectId(), created);
        	return created;
        }
        else
        {
        	return new com.hello2morrow.ddaexample.business.user.data.trans.UserDataSupplier();
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

	public com.hello2morrow.ddaexample.business.user.dsi.UserDsi[] findAllUsers()
	{
		return getAll();
	}
	
	public com.hello2morrow.ddaexample.business.user.dsi.UserDsi findUserByName(java.lang.String name) 
		throws TechnicalException
	{
		UserDsi[] all = getAll();
        for (int i = 0; i < all.length; i++)
        {
            UserDsi next = all[i];
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