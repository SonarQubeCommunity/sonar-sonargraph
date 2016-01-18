/**
 * 23.05.2005 17:28:18 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.user.domain.User'
 */

package com.hello2morrow.ddaexample.business.user.service;

public class UserDto extends com.hello2morrow.dda.business.common.service.DomainObjectDto
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

	private java.lang.String m_Password;
	
	public java.lang.String getPassword()
	{
		return m_Password;	
	}
	
	public void setPassword(java.lang.String password)
	{
		m_Password = password;	
	}

	private final java.util.List m_RolesReferences = new java.util.ArrayList();

    public void addRolesReference(com.hello2morrow.dda.foundation.common.ObjectIdIf ref)
    {
        assert ref != null;
        assert !m_RolesReferences.contains(ref);
        m_RolesReferences.add(ref);
    }

    public com.hello2morrow.dda.foundation.common.ObjectIdIf[] getRolesReferences()
    {
        return (com.hello2morrow.dda.foundation.common.ObjectIdIf[]) m_RolesReferences.toArray(new com.hello2morrow.dda.foundation.common.ObjectIdIf[0]);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("Name = ");
		buffer.append(getName());
        buffer.append(LINE_SEPARATOR);
	
		buffer.append("Password = ");
		buffer.append(getPassword());
        buffer.append(LINE_SEPARATOR);
	
		com.hello2morrow.dda.foundation.common.ObjectIdIf[] rolesReferences = getRolesReferences();

		buffer.append("RolesReferences (");
		buffer.append(rolesReferences.length);
		buffer.append(") = ");
		
		if(rolesReferences.length > 0)
		{
            buffer.append(LINE_SEPARATOR);

			for (int i = 0; i < rolesReferences.length; i++)
	        {
	            buffer.append(rolesReferences[i]);
	            buffer.append(LINE_SEPARATOR);
	        }
		}
		else
		{
        	buffer.append(LINE_SEPARATOR);
       	}

        return buffer.toString();
    }
}