/**
 * 23.05.2005 17:28:22 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.service;

public class SalesAssistantDto extends com.hello2morrow.ddaexample.business.contact.service.PersonDto
{
	private final java.util.List m_CustomersReferences = new java.util.ArrayList();

    public void addCustomersReference(com.hello2morrow.dda.foundation.common.ObjectIdIf ref)
    {
        assert ref != null;
        assert !m_CustomersReferences.contains(ref);
        m_CustomersReferences.add(ref);
    }

    public com.hello2morrow.dda.foundation.common.ObjectIdIf[] getCustomersReferences()
    {
        return (com.hello2morrow.dda.foundation.common.ObjectIdIf[]) m_CustomersReferences.toArray(new com.hello2morrow.dda.foundation.common.ObjectIdIf[0]);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		com.hello2morrow.dda.foundation.common.ObjectIdIf[] customersReferences = getCustomersReferences();

		buffer.append("CustomersReferences (");
		buffer.append(customersReferences.length);
		buffer.append(") = ");
		
		if(customersReferences.length > 0)
		{
            buffer.append(LINE_SEPARATOR);

			for (int i = 0; i < customersReferences.length; i++)
	        {
	            buffer.append(customersReferences[i]);
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