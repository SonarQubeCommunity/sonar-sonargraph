/**
 * 23.05.2005 17:28:19 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.contact.data.test;

import com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class PersonDataSupplier extends com.hello2morrow.dda.business.common.dsi.TestDataSupplier
	implements PersonDsi
{
	protected PersonDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	private java.lang.String m_FirstName = null; 

	public java.lang.String getFirstName()
	{
		return m_FirstName;
	}

	public void setFirstName(java.lang.String set)
	{
		m_FirstName = set;
	}

	private java.lang.String m_LastName = null; 

	public java.lang.String getLastName()
	{
		return m_LastName;
	}

	public void setLastName(java.lang.String set)
	{
		m_LastName = set;
	}

	private com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi m_Address = null; 

	public com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi getAddress()
	{
		return m_Address;
	}

	public void setAddress(com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi set)
	{
		m_Address = set;
	}

}