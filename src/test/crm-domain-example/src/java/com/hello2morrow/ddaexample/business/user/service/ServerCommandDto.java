/**
 * 23.05.2005 17:28:15 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.user.domain.ServerCommand'
 */

package com.hello2morrow.ddaexample.business.user.service;

public class ServerCommandDto extends com.hello2morrow.dda.business.common.service.DomainObjectDto
{
	private java.lang.String m_Name;
	
	public java.lang.String getName()
	{
		return m_Name;	
	}
	
	public void setName(java.lang.String name)
	{
		m_Name = name;	
	}

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("Name = ");
		buffer.append(getName());
        buffer.append(LINE_SEPARATOR);
	
        return buffer.toString();
    }
}