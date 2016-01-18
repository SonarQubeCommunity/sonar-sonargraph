/**
 * 23.05.2005 17:28:18 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.user.domain.User'
 */

package com.hello2morrow.ddaexample.business.user.data.trans;

import com.hello2morrow.ddaexample.business.user.dsi.UserDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class UserDataSupplier extends com.hello2morrow.dda.business.common.dsi.TransientDataSupplier implements UserDsi
{
	protected UserDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public UserDataSupplier()
	{
		super(new DomainObjectId(UserDsi.class));
	}

	private java.lang.String m_Name = null; 

	public java.lang.String getName()
	{
		return m_Name;
	}

	public void setName(java.lang.String set)
	{
		m_Name = set;
	}

	private java.lang.String m_Password = null; 

	public java.lang.String getPassword()
	{
		return m_Password;
	}

	public void setPassword(java.lang.String set)
	{
		m_Password = set;
	}

	private java.util.Set m_Roles = new java.util.HashSet();

	public com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[] getRoles()
	{
		return (com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[])m_Roles.toArray(new com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[0]);
	}

	public void setRoles(com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[] set)
	{
        assert com.hello2morrow.dda.foundation.common.exception.AssertionUtility.checkArray(set);
        m_Roles.clear();
        m_Roles.addAll(java.util.Arrays.asList(set));
	}

	public void addToRoles(com.hello2morrow.ddaexample.business.user.dsi.RoleDsi add) throws BusinessException
	{
		assert add != null;
		if ( !m_Roles.add(add) )
		{
			throw new BusinessException("unable to add object = " + add);
		}
	}

	public void removeFromRoles(com.hello2morrow.ddaexample.business.user.dsi.RoleDsi remove) throws BusinessException
	{
		assert remove != null;
		if ( !m_Roles.remove(remove) )
		{
			throw new BusinessException("unable to remove object = " + remove);
		}
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(UserDsi.class);
        assert dmi != null;
        UserDsi created = (UserDsi) dmi.createDataSupplier(UserDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		UserDsi concrete = (UserDsi)persistent;
		
		concrete.setName(getName());
		concrete.setPassword(getPassword());
		com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[] roles = getRoles();
		com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[] mapped = new com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[roles.length];
		
		for (int i = 0; i < roles.length; ++i)
		{
			com.hello2morrow.ddaexample.business.user.dsi.RoleDsi nextFromRoles = roles[i];
			if(!nextFromRoles.supportsPersistentData())
			{
				mapped[i] = (com.hello2morrow.ddaexample.business.user.dsi.RoleDsi)((com.hello2morrow.dda.business.common.dsi.TransientDataSupplier)nextFromRoles).save();
			}
			else
			{
				mapped[i] = nextFromRoles; 
			}
		} 		

		concrete.setRoles(mapped);
	
	}
}