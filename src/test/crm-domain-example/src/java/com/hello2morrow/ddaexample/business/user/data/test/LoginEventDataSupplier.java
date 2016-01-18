/**
 * 23.05.2005 17:28:18 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.user.domain.LoginEvent'
 */

package com.hello2morrow.ddaexample.business.user.data.test;

import com.hello2morrow.ddaexample.business.user.dsi.LoginEventDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class LoginEventDataSupplier extends com.hello2morrow.dda.business.common.dsi.TestDataSupplier
	implements LoginEventDsi
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

}