/**
 * 23.05.2005 17:28:20 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.request.domain.Request'
 */

package com.hello2morrow.ddaexample.business.request.data.test;

import com.hello2morrow.ddaexample.business.request.dsi.RequestDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class RequestDataSupplier extends com.hello2morrow.dda.business.common.dsi.TestDataSupplier
	implements RequestDsi
{
	protected RequestDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	private java.lang.String m_Subject = null; 

	public java.lang.String getSubject()
	{
		return m_Subject;
	}

	public void setSubject(java.lang.String set)
	{
		m_Subject = set;
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

	private com.hello2morrow.ddaexample.business.request.dsi.StateDsi m_State = null; 

	public com.hello2morrow.ddaexample.business.request.dsi.StateDsi getState()
	{
		return m_State;
	}

	public void setState(com.hello2morrow.ddaexample.business.request.dsi.StateDsi set)
	{
		m_State = set;
	}

}