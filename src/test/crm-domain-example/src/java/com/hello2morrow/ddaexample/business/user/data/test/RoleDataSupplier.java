/**
 * 23.05.2005 17:28:17 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.user.domain.Role'
 */

package com.hello2morrow.ddaexample.business.user.data.test;

import com.hello2morrow.ddaexample.business.user.dsi.RoleDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class RoleDataSupplier extends com.hello2morrow.dda.business.common.dsi.TestDataSupplier
	implements RoleDsi
{
	protected RoleDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public RoleDataSupplier()
	{
		super(new DomainObjectId(RoleDsi.class));
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

	private java.util.Set m_ServerCommands = new java.util.HashSet();

	public com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[] getServerCommands()
	{
		return (com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[])m_ServerCommands.toArray(new com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[0]);
	}

	public void setServerCommands(com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[] set)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
        assert com.hello2morrow.dda.foundation.common.exception.AssertionUtility.checkArray(set);
        m_ServerCommands.clear();
        for (int i = 0; i < set.length; i++)
        {
            addToServerCommands(set[i]);
        }
	}

	public void addToServerCommands(com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi add)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
		assert add != null;
		if ( !m_ServerCommands.add(add) )
		{
			throw new com.hello2morrow.dda.foundation.common.exception.BusinessException("unable to add object = " + add);
		}
	}

	public void removeFromServerCommands(com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi remove)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException
	{
		assert remove != null;
		if ( !m_ServerCommands.remove(remove) )
		{
			throw new com.hello2morrow.dda.foundation.common.exception.BusinessException("unable to remove object = " + remove);
		}
	}

}