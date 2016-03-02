/**
 * 23.05.2005 17:28:18 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.user.domain.LoginEvent'
 */

package com.hello2morrow.ddaexample.business.user.service;

public class LoginEventDto extends com.hello2morrow.dda.business.common.service.DomainObjectDto
{
	private java.lang.String m_UserName;
	
	public java.lang.String getUserName()
	{
		return m_UserName;	
	}
	
	public void setUserName(java.lang.String userName)
	{
		m_UserName = userName;	
	}

	private java.lang.String m_CreatedTimestamp;
	
	public java.lang.String getCreatedTimestamp()
	{
		return m_CreatedTimestamp;	
	}
	
	public void setCreatedTimestamp(java.lang.String createdTimestamp)
	{
		m_CreatedTimestamp = createdTimestamp;	
	}

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("UserName = ");
		buffer.append(getUserName());
        buffer.append(LINE_SEPARATOR);
	
		buffer.append("CreatedTimestamp = ");
		buffer.append(getCreatedTimestamp());
        buffer.append(LINE_SEPARATOR);
	
        return buffer.toString();
    }
}