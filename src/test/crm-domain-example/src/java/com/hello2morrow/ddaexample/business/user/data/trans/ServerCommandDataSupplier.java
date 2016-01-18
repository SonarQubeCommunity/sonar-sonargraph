/**
 * 23.05.2005 17:28:16 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.user.domain.ServerCommand'
 */

package com.hello2morrow.ddaexample.business.user.data.trans;

import com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class ServerCommandDataSupplier extends com.hello2morrow.dda.business.common.dsi.TransientDataSupplier implements ServerCommandDsi
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

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(ServerCommandDsi.class);
        assert dmi != null;
        ServerCommandDsi created = (ServerCommandDsi) dmi.createDataSupplier(ServerCommandDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		ServerCommandDsi concrete = (ServerCommandDsi)persistent;
		
	}
}