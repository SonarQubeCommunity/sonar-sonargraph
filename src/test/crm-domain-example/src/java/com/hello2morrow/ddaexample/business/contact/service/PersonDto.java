/**
 * 23.05.2005 17:28:19 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.contact.service;

public class PersonDto extends com.hello2morrow.dda.business.common.service.DomainObjectDto
{
	private java.lang.String m_FirstName;
	
	public java.lang.String getFirstName()
	{
		return m_FirstName;	
	}
	
	public void setFirstName(java.lang.String firstName)
	{
		m_FirstName = firstName;	
	}

	private java.lang.String m_LastName;
	
	public java.lang.String getLastName()
	{
		return m_LastName;	
	}
	
	public void setLastName(java.lang.String lastName)
	{
		m_LastName = lastName;	
	}

	private com.hello2morrow.dda.foundation.common.ObjectIdIf m_AddressReference;

    public void setAddressReference(com.hello2morrow.dda.foundation.common.ObjectIdIf ref)
    {
        assert ref != null;
        m_AddressReference = ref;
    }

    public com.hello2morrow.dda.foundation.common.ObjectIdIf getAddressReference()
    {
        return m_AddressReference;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("FirstName = ");
		buffer.append(getFirstName());
        buffer.append(LINE_SEPARATOR);
	
		buffer.append("LastName = ");
		buffer.append(getLastName());
        buffer.append(LINE_SEPARATOR);
	
		buffer.append("AddressReference = ");
		buffer.append(getAddressReference());
        buffer.append(LINE_SEPARATOR);

        return buffer.toString();
    }
}