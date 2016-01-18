/**
 * 23.05.2005 17:28:18 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.user.domain.User'
 */

package com.hello2morrow.ddaexample.business.user.data.test;

import com.hello2morrow.ddaexample.business.user.dsi.UserDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class UserDataSupplier extends com.hello2morrow.dda.business.common.dsi.TestDataSupplier
	implements UserDsi
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
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
        assert com.hello2morrow.dda.foundation.common.exception.AssertionUtility.checkArray(set);
        m_Roles.clear();
        for (int i = 0; i < set.length; i++)
        {
            addToRoles(set[i]);
        }
	}

	public void addToRoles(com.hello2morrow.ddaexample.business.user.dsi.RoleDsi add)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
		assert add != null;
		if ( !m_Roles.add(add) )
		{
			throw new com.hello2morrow.dda.foundation.common.exception.BusinessException("unable to add object = " + add);
		}
	}

	public void removeFromRoles(com.hello2morrow.ddaexample.business.user.dsi.RoleDsi remove)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
		assert remove != null;
		if ( !m_Roles.remove(remove) )
		{
			throw new com.hello2morrow.dda.foundation.common.exception.BusinessException("unable to remove object = " + remove);
		}
	}

}