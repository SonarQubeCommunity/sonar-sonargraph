/**
 * 23.05.2005 17:28:21 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.customer.domain.Customer'
 */

package com.hello2morrow.ddaexample.business.customer.service;

public class CustomerDto extends com.hello2morrow.ddaexample.business.contact.service.PersonDto
{
	private int m_Age;
	
	public int getAge()
	{
		return m_Age;	
	}
	
	public void setAge(int age)
	{
		m_Age = age;	
	}

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("Age = ");
		buffer.append(getAge());
        buffer.append(LINE_SEPARATOR);
	
        return buffer.toString();
    }
}