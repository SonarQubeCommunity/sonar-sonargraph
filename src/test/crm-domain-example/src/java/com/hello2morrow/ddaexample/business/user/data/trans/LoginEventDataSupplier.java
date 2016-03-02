/**
 * 23.05.2005 17:28:18 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.user.domain.LoginEvent'
 */

package com.hello2morrow.ddaexample.business.user.data.trans;

import com.hello2morrow.ddaexample.business.user.dsi.LoginEventDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class LoginEventDataSupplier extends com.hello2morrow.dda.business.common.dsi.TransientDataSupplier implements LoginEventDsi
{
	protected LoginEventDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public LoginEventDataSupplier()
	{
		super(new DomainObjectId(LoginEventDsi.class));
	}

	private java.lang.String m_UserName = null; 

	public java.lang.String getUserName()
	{
		return m_UserName;
	}

	public void setUserName(java.lang.String set)
	{
		m_UserName = set;
	}

	private java.lang.String m_CreatedTimestamp = null; 

	public java.lang.String getCreatedTimestamp()
	{
		return m_CreatedTimestamp;
	}

	public void setCreatedTimestamp(java.lang.String set)
	{
		m_CreatedTimestamp = set;
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(LoginEventDsi.class);
        assert dmi != null;
        LoginEventDsi created = (LoginEventDsi) dmi.createDataSupplier(LoginEventDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		LoginEventDsi concrete = (LoginEventDsi)persistent;
		
	}
}