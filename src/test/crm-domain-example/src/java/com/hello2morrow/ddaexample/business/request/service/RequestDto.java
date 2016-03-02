/**
 * 23.05.2005 17:28:20 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.request.domain.Request'
 */

package com.hello2morrow.ddaexample.business.request.service;

public class RequestDto extends com.hello2morrow.dda.business.common.service.DomainObjectDto
{
	private java.lang.String m_Subject;
	
	public java.lang.String getSubject()
	{
		return m_Subject;	
	}
	
	public void setSubject(java.lang.String subject)
	{
		m_Subject = subject;	
	}

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("Subject = ");
		buffer.append(getSubject());
        buffer.append(LINE_SEPARATOR);
	
        return buffer.toString();
    }
}