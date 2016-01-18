/**
 * 23.05.2005 17:28:16 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.user.domain.ServerCommand'
 */

package com.hello2morrow.ddaexample.business.user.data.test;

import com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class ServerCommandDataSupplier extends com.hello2morrow.dda.business.common.dsi.TestDataSupplier
	implements ServerCommandDsi
{
	protected ServerCommandDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public ServerCommandDataSupplier()
	{
		super(new DomainObjectId(ServerCommandDsi.class));
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

}