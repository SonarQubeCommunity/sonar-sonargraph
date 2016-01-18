/**
 * 23.05.2005 17:28:21 - generated 'data-supplier-test' for 'com.hello2morrow.ddaexample.business.contact.domain.Address'
 */

package com.hello2morrow.ddaexample.business.contact.data.test;

import com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;

public class AddressDataSupplier extends com.hello2morrow.dda.business.common.dsi.TestDataSupplier
	implements AddressDsi
{
	protected AddressDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public AddressDataSupplier()
	{
		super(new DomainObjectId(AddressDsi.class));
	}

	private java.lang.String m_Street = null; 

	public java.lang.String getStreet()
	{
		return m_Street;
	}

	public void setStreet(java.lang.String set)
	{
		m_Street = set;
	}

	private java.lang.String m_City = null; 

	public java.lang.String getCity()
	{
		return m_City;
	}

	public void setCity(java.lang.String set)
	{
		m_City = set;
	}

	private java.lang.String m_ZipCode = null; 

	public java.lang.String getZipCode()
	{
		return m_ZipCode;
	}

	public void setZipCode(java.lang.String set)
	{
		m_ZipCode = set;
	}

}