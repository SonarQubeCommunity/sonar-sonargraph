/**
 * 23.05.2005 17:28:21 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.contact.domain.Address'
 */

package com.hello2morrow.ddaexample.business.contact.service;

public class AddressDto extends com.hello2morrow.dda.business.common.service.DomainObjectDto
{
	private java.lang.String m_Street;
	
	public java.lang.String getStreet()
	{
		return m_Street;	
	}
	
	public void setStreet(java.lang.String street)
	{
		m_Street = street;	
	}

	private java.lang.String m_City;
	
	public java.lang.String getCity()
	{
		return m_City;	
	}
	
	public void setCity(java.lang.String city)
	{
		m_City = city;	
	}

	private java.lang.String m_ZipCode;
	
	public java.lang.String getZipCode()
	{
		return m_ZipCode;	
	}
	
	public void setZipCode(java.lang.String zipCode)
	{
		m_ZipCode = zipCode;	
	}

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("Street = ");
		buffer.append(getStreet());
        buffer.append(LINE_SEPARATOR);
	
		buffer.append("City = ");
		buffer.append(getCity());
        buffer.append(LINE_SEPARATOR);
	
		buffer.append("ZipCode = ");
		buffer.append(getZipCode());
        buffer.append(LINE_SEPARATOR);
	
        return buffer.toString();
    }
}