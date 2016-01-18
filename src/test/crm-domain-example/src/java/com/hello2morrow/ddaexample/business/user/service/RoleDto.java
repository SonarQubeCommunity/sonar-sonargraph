/**
 * 23.05.2005 17:28:17 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.user.domain.Role'
 */

package com.hello2morrow.ddaexample.business.user.service;

public class RoleDto extends com.hello2morrow.dda.business.common.service.DomainObjectDto
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

	private final java.util.List m_ServerCommandsReferences = new java.util.ArrayList();

    public void addServerCommandsReference(com.hello2morrow.dda.foundation.common.ObjectIdIf ref)
    {
        assert ref != null;
        assert !m_ServerCommandsReferences.contains(ref);
        m_ServerCommandsReferences.add(ref);
    }

    public com.hello2morrow.dda.foundation.common.ObjectIdIf[] getServerCommandsReferences()
    {
        return (com.hello2morrow.dda.foundation.common.ObjectIdIf[]) m_ServerCommandsReferences.toArray(new com.hello2morrow.dda.foundation.common.ObjectIdIf[0]);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("Name = ");
		buffer.append(getName());
        buffer.append(LINE_SEPARATOR);
	
		com.hello2morrow.dda.foundation.common.ObjectIdIf[] serverCommandsReferences = getServerCommandsReferences();

		buffer.append("ServerCommandsReferences (");
		buffer.append(serverCommandsReferences.length);
		buffer.append(") = ");
		
		if(serverCommandsReferences.length > 0)
		{
            buffer.append(LINE_SEPARATOR);

			for (int i = 0; i < serverCommandsReferences.length; i++)
	        {
	            buffer.append(serverCommandsReferences[i]);
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