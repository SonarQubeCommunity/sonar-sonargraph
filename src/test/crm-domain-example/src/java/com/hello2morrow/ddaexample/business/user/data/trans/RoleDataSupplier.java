/**
 * 23.05.2005 17:28:17 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.user.domain.Role'
 */

package com.hello2morrow.ddaexample.business.user.data.trans;

import com.hello2morrow.ddaexample.business.user.dsi.RoleDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class RoleDataSupplier extends com.hello2morrow.dda.business.common.dsi.TransientDataSupplier implements RoleDsi
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
	{
        assert com.hello2morrow.dda.foundation.common.exception.AssertionUtility.checkArray(set);
        m_ServerCommands.clear();
        m_ServerCommands.addAll(java.util.Arrays.asList(set));
	}

	public void addToServerCommands(com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi add) throws BusinessException
	{
		assert add != null;
		if ( !m_ServerCommands.add(add) )
		{
			throw new BusinessException("unable to add object = " + add);
		}
	}

	public void removeFromServerCommands(com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi remove) throws BusinessException
	{
		assert remove != null;
		if ( !m_ServerCommands.remove(remove) )
		{
			throw new BusinessException("unable to remove object = " + remove);
		}
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(RoleDsi.class);
        assert dmi != null;
        RoleDsi created = (RoleDsi) dmi.createDataSupplier(RoleDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		RoleDsi concrete = (RoleDsi)persistent;
		
		com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[] serverCommands = getServerCommands();
		com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[] mapped = new com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[serverCommands.length];
		
		for (int i = 0; i < serverCommands.length; ++i)
		{
			com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi nextFromServerCommands = serverCommands[i];
			if(!nextFromServerCommands.supportsPersistentData())
			{
				mapped[i] = (com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi)((com.hello2morrow.dda.business.common.dsi.TransientDataSupplier)nextFromServerCommands).save();
			}
			else
			{
				mapped[i] = nextFromServerCommands; 
			}
		} 		

		concrete.setServerCommands(mapped);
	
	}
}